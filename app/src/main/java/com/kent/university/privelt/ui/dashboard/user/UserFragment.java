/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.ui.dashboard.user;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kent.university.privelt.R;
import com.kent.university.privelt.base.BaseFragment;
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
import butterknife.BindView;
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

    @BindView(R.id.first_name_tv)
    @SetterMethod(method = "setFirstName")
    TextView firstNameTv;

    @BindView(R.id.last_name_tv)
    @SetterMethod(method = "setLastName")
    TextView lastNameTv;

    @BindView(R.id.birthday_tv)
    @SetterMethod(method = "setBirthday")
    TextView birthdayTv;

    @BindView(R.id.address_tv)
    @SetterMethod(method = "setAddress")
    TextView addressTv;

    @BindView(R.id.phone_number_tv)
    @SetterMethod(method = "setPhoneNumber")
    TextView phoneNumberTv;

    @BindView(R.id.mail_tv)
    @SetterMethod(method = "setMail")
    TextView mailTv;

    @BindView(R.id.edit_texts)
    LinearLayout editTexts;

    @BindView(R.id.text_views)
    LinearLayout textViews;

    private UserViewModel userViewModel;

    private CurrentUser currentUser;
    private Menu mOptionsMenu;

    @OnClick(R.id.birthday)
    void onBirthdayClick() {


        DatePickerDialog.OnDateSetListener listener = (datePicker, dayOfMonth, monthOfYear, year) -> birthday.setText(new StringBuilder()
                .append(dayOfMonth).append("/").append(monthOfYear + 1).append("/").append(year).append(" "));

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(getContext(), listener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void configureEditTexts() {
        editTexts.setVisibility(View.INVISIBLE);
        this.onFieldsAction((s) -> {
            if (s.second instanceof EditText)
                s.second.addTextChangedListener(new UserTextWatcher((EditText) s.second, this));
        });
    }

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_user;
    }

    @Override
    protected void configureViewModel() {
        userViewModel = getViewModel(UserViewModel.class);
        userViewModel.init();
    }

    @Override
    protected void configureDesign() {
        getCurrentUser();

        configureEditTexts();
    }

    private void getCurrentUser() {
        userViewModel.getCurrentUser().observe(this, this::updateCurrentUser);
    }

    private void updateCurrentUser(CurrentUser currentUser) {
        this.currentUser = currentUser;
        if (currentUser == null)
            this.currentUser = new CurrentUser("", "", "", "", "", "");
        this.onFieldsAction((pair) -> {
            String m = pair.first.getAnnotation(SetterMethod.class).method().replaceFirst("s", "g");
            for (final Method method : this.currentUser.getClass().getMethods()) {
                if (method.getName().equalsIgnoreCase(m)) {
                    try {
                        pair.second.setText((CharSequence) method.invoke(this.currentUser));
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
    public void afterTextChanged(EditText editText, Editable editable) {
        this.onFieldsAction((pair) -> {
            if (currentUser == null)
                return;
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

    private void onFieldsAction(ValueCallback<Pair<Field, TextView>> callback, Object... objects) {
        final Field[] fields = getClass().getDeclaredFields();
        for (final Field field : fields) {
            if (!field.isAnnotationPresent(SetterMethod.class))
                continue;

            SetterMethod setterMethod = field.getAnnotation(SetterMethod.class);

            if (setterMethod == null || setterMethod.method().equals(""))
                continue;

            try {
                if (objects.length > 0 && field.get(this) == objects[0] || objects.length == 0)
                    callback.onReceiveValue(new Pair<>(field, (TextView) field.get(this)));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            textViews.setVisibility(View.INVISIBLE);
            editTexts.setVisibility(View.VISIBLE);
            mOptionsMenu.findItem(R.id.edit).setVisible(false);
            mOptionsMenu.findItem(R.id.check).setVisible(true);
            mOptionsMenu.findItem(R.id.settings).setVisible(false);
        }
        else if (item.getItemId() == R.id.check) {
            textViews.setVisibility(View.VISIBLE);
            editTexts.setVisibility(View.INVISIBLE);
            mOptionsMenu.findItem(R.id.edit).setVisible(true);
            mOptionsMenu.findItem(R.id.check).setVisible(false);
            mOptionsMenu.findItem(R.id.settings).setVisible(true);
            userViewModel.updateCurrentUser(currentUser);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        mOptionsMenu = menu;
    }
}
