package org.digitalcampus.mobile.learning.activity;

import org.apache.commons.validator.EmailValidator;
import org.digitalcampus.mobile.learning.application.MobileLearning;
import org.digitalcampus.mobile.learning.listener.SubmitListener;
import org.digitalcampus.mobile.learning.model.User;
import org.digitalcampus.mobile.learning.task.LoginTask;
import org.digitalcampus.mobile.learning.task.Payload;
import org.digitalcampus.mobile.learning.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity implements SubmitListener  {

	public static final String TAG = "LoginActivity";
	private SharedPreferences prefs;
	
	private EditText usernameField;
	private EditText passwordField;
	private ProgressDialog pDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		usernameField = (EditText) findViewById(R.id.login_username_field);
        passwordField = (EditText) findViewById(R.id.login_password_field);
	}
	
	public void onLoginClick(View view){
		String username = usernameField.getText().toString();
    	//check valid email address format
    	if(username.length() == 0){
    		MobileLearning.showAlert(this,R.string.error,R.string.error_no_username);
    		return;
    	}
    	
    	// get text from email
    	String password = passwordField.getText().toString();
    	//check length
    	if(password.length()< MobileLearning.PASSWORD_MIN_LENGTH ){
    		MobileLearning.showAlert(this,R.string.error,getString(R.string.error_password_length,MobileLearning.PASSWORD_MIN_LENGTH));
    		return;
    	}
    	
    	// show progress dialog
    	// TODO set proper lang strings
        pDialog = new ProgressDialog(this);
        pDialog.setTitle(R.string.title_login);
        pDialog.setMessage("Logging in...");
        pDialog.setCancelable(true);
        pDialog.show();
        
    	User[] u = new User[1];
    	u[0] = new User();
    	u[0].username = username;
    	u[0].password = password;
    	Payload p = new Payload(0,u);
    	LoginTask lt = new LoginTask(this);
    	lt.setLoginListener(this);
    	lt.execute(p);
	}
	
	public void onRegisterClick(View view){
		Intent i = new Intent(this, RegisterActivity.class);
		startActivity(i);
		finish();
		
	}

	public void submitComplete(Payload response) {
		pDialog.dismiss();
		Log.d(TAG,"Login activity reports: " + response.resultResponse);
		if(response.result){
			// set params
			Editor editor = prefs.edit();
	    	editor.putString("prefUsername", usernameField.getText().toString());
	    	editor.putString("prefPassword", passwordField.getText().toString());
	    	editor.commit();
	    	
			// return to main activity
			finish();
		} else {
			MobileLearning.showAlert(this, R.string.title_login, response.resultResponse);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent i = new Intent(this, PrefsActivity.class);
			startActivity(i);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

