package com.kent.university.privelt.ui.dashboard.user;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;

import android.widget.EditText;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseFragment;
import com.kent.university.privelt.database.injections.Injection;
import com.kent.university.privelt.database.injections.ViewModelFactory;
import com.kent.university.privelt.model.CurrentUser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@interface SetterMethod {
    String method() default "";
}

public class UserFragment extends BaseFragment implements UserTextWatcher.MyTextWatcher {

    @BindView(R.id.first_name)
    @SetterMethod(method = "setFirstName")
    EditText firstName;

    @BindView(R.id.last_name)
    @SetterMethod(method = "setLastName")
    EditText lastName;

    @BindView(R.id.birthday)
    @SetterMethod(method = "setBirthday")
    EditText birthday;

    @BindView(R.id.address)
    @SetterMethod(method = "setAddress")
    EditText address;

    @BindView(R.id.phone_number)
    @SetterMethod(method = "setPhoneNumber")
    EditText phoneNumber;

    @BindView(R.id.mail)
    @SetterMethod(method = "setMail")
    EditText mail;

    private UserViewModel userViewModel;

    private CurrentUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);

        configureViewModel();

        getCurrentUser();

        configureEditTexts();

        return view;
    }

    ;

    @OnClick(R.id.birthday)
    public void onBirthdayClick(View view) {


        DatePickerDialog.OnDateSetListener listener = (datePicker, dayOfMonth, monthOfYear, year) -> {
            // TODO Auto-generated method stub
            birthday.setText(new StringBuilder()
                    // Month is 0 based so add 1
                    .append(dayOfMonth).append("/").append(monthOfYear + 1).append("/").append(year).append(" "));
        };

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(getContext(), listener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void configureEditTexts() {
        this.onFieldsAction((s) -> s.second.addTextChangedListener(new UserTextWatcher(s.second, this)));
    }

    private void configureViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(getContext());
        userViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserViewModel.class);
        userViewModel.init();
    }

    private void getCurrentUser() {
        userViewModel.getCurrentUser().observe(this, this::updateCurrentUser);
    }

    private void updateCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
        this.onFieldsAction((pair) -> {
            String m = pair.first.getAnnotation(SetterMethod.class).method().replaceFirst("s", "g");
            for (final Method method : currentUser.getClass().getMethods()) {
                if (method.getName().equalsIgnoreCase(m)) {
                    try {
                        pair.second.setText((CharSequence) method.invoke(currentUser));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void afterTextChanged(EditText editText, Editable editable) {
        this.onFieldsAction((pair) -> {
            String m = pair.first.getAnnotation(SetterMethod.class).method();
            for (final Method method : currentUser.getClass().getMethods()) {
                if (method.getName().equalsIgnoreCase(m)) {
                    try {
                        method.invoke(currentUser, editable.toString());
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, editText);
    }

    private void onFieldsAction(ValueCallback<Pair<Field, EditText>> callback, Object... objects) {
        final Field[] fields = getClass().getDeclaredFields();
        for (final Field field : fields) {
            if (!field.isAnnotationPresent(SetterMethod.class))
                continue;

            SetterMethod setterMethod = field.getAnnotation(SetterMethod.class);

            if (setterMethod == null || setterMethod.method().equals(""))
                continue;

            try {
                if (objects.length > 0 && field.get(this) == objects[0] || objects.length == 0)
                    callback.onReceiveValue(new Pair<>(field, (EditText) field.get(this)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        userViewModel.updateCurrentUser(currentUser);
    }
}
