package org.digitalcampus.oppia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import org.digitalcampus.mobile.learning.R;
import org.digitalcampus.mobile.learning.databinding.ActivityPrivacyBinding;
import org.digitalcampus.oppia.analytics.Analytics;
import org.digitalcampus.oppia.application.SessionManager;
import org.digitalcampus.oppia.fragments.DeleteAccountDialogFragment;
import org.digitalcampus.oppia.fragments.DownloadUserDataDialogFragment;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

public class PrivacyActivity extends AppActivity implements DeleteAccountDialogFragment.DeleteAccountListener {

    private ActivityPrivacyBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        getAppComponent().inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();

        binding.aboutPrivacyPolicy.setOnClickListener(view -> launchAboutPage(AboutActivity.TAB_PRIVACY_POLICY));
        binding.aboutPrivacyWhat.setOnClickListener(view -> launchAboutPage(AboutActivity.TAB_PRIVACY_WHAT));
        binding.aboutPrivacyWhy.setOnClickListener(view -> launchAboutPage(AboutActivity.TAB_PRIVACY_HOW));
        binding.aboutPrivacyTerms.setOnClickListener(view -> launchAboutPage(AboutActivity.TAB_PRIVACY_TERMS));

        binding.analyticsCheckbox.setChecked(Analytics.isTrackingEnabled(this));
        binding.analyticsCheckbox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()){
                Analytics.enableTracking(this);
            }
            else{
                Analytics.disableTracking(this);
            }
        });

        binding.bugreportCheckbox.setChecked(Analytics.isBugReportEnabled(this));
        binding.bugreportCheckbox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (compoundButton.isChecked()){
                Analytics.enableBugReport(this);
            }
            else{
                Analytics.disableBugReport(this);
            }
        });


        if (!SessionManager.isLoggedIn(this)){
            findViewById(R.id.privacy_user_section).setVisibility(View.GONE);
            return;
        }

        Button deleteBtn = findViewById(R.id.btn_delete_account);
        deleteBtn.setOnClickListener(v -> showDeleteAccountWarning());
        Button downloadBtn = findViewById(R.id.btn_download_data);
        downloadBtn.setOnClickListener(v -> showDownloadDataDialog());

    }

    private void launchAboutPage(int tab){
        Intent i = new Intent(this, AboutActivity.class);
        Bundle tb = new Bundle();
        tb.putString(AboutActivity.TITLE, getString(R.string.privacy_section_privacy));
        tb.putString(AboutActivity.ABOUT_CONTENTS, AboutActivity.ABOUT_PRIVACY);
        tb.putInt(AboutActivity.TAB_ACTIVE, tab);
        i.putExtras(tb);
        startActivity(i);
    }

    private void showDeleteAccountWarning(){

        FragmentManager fm = getSupportFragmentManager();
        DeleteAccountDialogFragment deleteDialog = DeleteAccountDialogFragment.newInstance();
        deleteDialog.show(fm, "fragment_delete_account");

    }

    private void showDownloadDataDialog(){
        FragmentManager fm = getSupportFragmentManager();
        DownloadUserDataDialogFragment downloadDialog = DownloadUserDataDialogFragment.newInstance();
        downloadDialog.show(fm, "fragment_download_data");
    }


    @Override
    public void onDeleteAccountSuccess() {
        logoutAndRestartApp();
    }
}
