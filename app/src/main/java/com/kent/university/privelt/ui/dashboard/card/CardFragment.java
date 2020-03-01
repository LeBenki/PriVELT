package com.kent.university.privelt.ui.dashboard.card;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kent.university.privelt.R;
import com.kent.university.privelt.api.ServiceHelper;
import com.kent.university.privelt.base.BaseFragment;
import com.kent.university.privelt.database.injections.Injection;
import com.kent.university.privelt.database.injections.ViewModelFactory;
import com.kent.university.privelt.events.ChangeWatchListStatusEvent;
import com.kent.university.privelt.events.DetailedCardEvent;
import com.kent.university.privelt.events.UpdateCredentialsEvent;
import com.kent.university.privelt.model.Service;
import com.kent.university.privelt.model.UserData;
import com.kent.university.privelt.ui.dashboard.DashboardActivity;
import com.kent.university.privelt.ui.detailed.DetailedCardActivity;
import com.kent.university.privelt.ui.login.LoginActivity;
import com.kent.university.privelt.ui.risk_value.RiskValueActivity;
import com.kent.university.privelt.utils.CardManager;
import com.kent.university.privelt.utils.WatchListHelper;
import com.kent.university.privelt.utils.sentence.SentenceAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kent.university.privelt.api.DataExtraction.processDataExtraction;
import static com.kent.university.privelt.ui.dashboard.card.FilterAlertDialog.KEY_SHARED;
import static com.kent.university.privelt.ui.detailed.DetailedCardActivity.PARAM_CARD;
import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_PASSWORD;
import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_SERVICE;
import static com.kent.university.privelt.ui.login.LoginActivity.PARAM_USER;

public class CardFragment extends BaseFragment implements FilterAlertDialog.FilterDialogListener {

    private static final int REQUEST_LOGIN = 765;
    private static final int REQUEST_EDIT_LOGIN = 7654;

    private CardViewModel cardViewModel;

    private ArrayList<Service> subscribedServices;

    private ArrayList<UserData> userDatas;

    @BindView(R.id.recycler_view_services)
    RecyclerView servicesList;

    @BindView(R.id.no_services)
    TextView noService;

    @BindView(R.id.add_service)
    FloatingActionButton addService;

    @BindView(R.id.global_privacy_value)
    TextView privacyValue;

    @BindView(R.id.risk_progress_overall)
    ProgressBar progressBar;

    private WatchListHelper watchListHelper;

    private CardAdapter cardAdapter;

    private boolean[] filters;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_service, container, false);

        ButterKnife.bind(this, view);

        setUpRecyclerView();
        setUpSwipe();
        setupAddButton();

        configureViewModel();
        getServices();
        getUserDatas();

        progressBar.setOnClickListener((v) -> startActivity(new Intent(getActivity(), RiskValueActivity.class)));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        filters = FilterAlertDialog.getFilters(getActivity().getSharedPreferences(KEY_SHARED, Context.MODE_PRIVATE));
        watchListHelper = new WatchListHelper(getActivity().getSharedPreferences(KEY_SHARED, Context.MODE_PRIVATE));
    }

    private void getUserDatas() {
        cardViewModel.getUserDatas().observe(this, this::updateUserDatas);
    }

    private void updateUserDatas(List<UserData> userData) {
        userDatas = new ArrayList<>(userData);
        updateOverallRiskValue();
        updateRecyclerView();
    }

    private void getServices() {
        cardViewModel.getServices().observe(this, this::updateServices);
    }

    private void updateServices(List<Service> services) {

        subscribedServices = new ArrayList<>(services);
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        if (subscribedServices.size() == 0)
            noService.setVisibility(View.VISIBLE);
        else {
            noService.setVisibility(View.GONE);
            cardAdapter.updateCards(CardManager.cardsFilter(userDatas, subscribedServices, filters, watchListHelper.getWatchList()));
            cardAdapter.notifyDataSetChanged();
        }
    }

    private void updateOverallRiskValue() {
        int riskValue = userDatas.size();

        if (riskValue > 100)
            riskValue = 100;

        if (riskValue < 20)
            privacyValue.setText(SentenceAdapter.adapt(getResources().getString(R.string.global_privacy_value), "Low"));
        else if (riskValue < 60)
            privacyValue.setText(SentenceAdapter.adapt(getResources().getString(R.string.global_privacy_value), "Medium"));
        else
            privacyValue.setText(SentenceAdapter.adapt(getResources().getString(R.string.global_privacy_value), "High"));

        progressBar.setProgress(riskValue);
    }

    private void setupAddButton() {

        addService.setOnClickListener(view -> {

            List<String> services = getServiceHelper().getRemainingServices(subscribedServices);

            if (services.isEmpty()) {
                Toast.makeText(CardFragment.this.getContext(), R.string.already_added_all, Toast.LENGTH_LONG).show();
                return;
            }

            final ArrayAdapter<String> adp = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, getServiceHelper().getRemainingServices(subscribedServices));

            final Spinner sp = new Spinner(getContext());
            sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            sp.setPadding(50, 50, 50, 50);
            sp.setAdapter(adp);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.choose_service);
            builder.setView(sp);
            builder.setPositiveButton(R.string.choose, (dialogInterface, i) -> editCredentials(
                    new Service(sp.getSelectedItem().toString(), false, "", "", ""), REQUEST_LOGIN));
            builder.create().show();
        });
    }

    private void editCredentials(Service service, int requestCode) {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.putExtra(PARAM_SERVICE, service);
        startActivityForResult(intent, requestCode);
    }

    private void setUpRecyclerView() {

        subscribedServices = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        servicesList.setLayoutManager(layoutManager);
        cardAdapter = new CardAdapter();
        servicesList.setAdapter(cardAdapter);
    }

    private void setUpSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.unsubscribe)
                        .setMessage(R.string.unsubscribe_confirmation)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            int position = viewHolder.getAdapterPosition();
                            cardViewModel.deleteService(subscribedServices.get(position));
                        })
                        .setNegativeButton(R.string.no, (dialogInterface, i) -> cardAdapter.notifyDataSetChanged())
                        .show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(servicesList);
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        cardViewModel = ViewModelProviders.of(this, viewModelFactory).get(CardViewModel.class);
        cardViewModel.init();
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_LOGIN || requestCode == REQUEST_EDIT_LOGIN) && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Service service = (Service) data.getSerializableExtra(PARAM_SERVICE);

                String user = data.getStringExtra(PARAM_USER);
                String password = data.getStringExtra(PARAM_PASSWORD);

                assert service != null;
                if (service.isPasswordSaved()) {

                    service.setUser(user);
                    service.setPassword(password);
                }

                new AsyncTask<Void, Void , Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        if ((requestCode == REQUEST_LOGIN)) {
                            cardViewModel.insertService(service);
                        } else {
                            cardViewModel.updateService(service);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (!service.isPasswordSaved())
                            processDataExtraction(new ServiceHelper(getContext()), service, user, password, getContext().getApplicationContext());
                        else
                            ((DashboardActivity)getActivity()).launchService();
                    }
                }.execute();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEditCredentials(UpdateCredentialsEvent event) {
        for (Service service: subscribedServices)
            if (service.getName().equals(event.service))
                editCredentials(service, REQUEST_EDIT_LOGIN);
    }

    @Subscribe
    public void onDetailedCard(DetailedCardEvent event) {
        Intent intent = new Intent(getContext(), DetailedCardActivity.class);
        intent.putExtra(PARAM_CARD, event.card);
        startActivity(intent);
    }

    @Subscribe
    public void onCardAddedToWatchList(ChangeWatchListStatusEvent event) {
        watchListHelper.changeWatchListStatus(event.cardName);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filter) {
            FilterAlertDialog cdf = new FilterAlertDialog(this);
            cdf.show(getFragmentManager(), "");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(boolean[] selectedItems) {
        filters = selectedItems;
        updateRecyclerView();
    }
}
