package com.n8yn8.farmersmarket.parse;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.n8yn8.farmersmarket.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LogInFragment extends Fragment {

	private String TAG = "ItemListFragment";

	Button loginbutton;
	Button signup;
	String usernametxt;
	String passwordtxt;
	EditText password;
	EditText username;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreatView");
		View rootView = inflater.inflate(R.layout.fragment_loginsignup, container, false);
		// Locate EditTexts in main.xml
		username = (EditText) rootView.findViewById(R.id.username);
		password = (EditText) rootView.findViewById(R.id.password);

		// Locate Buttons in main.xml
		loginbutton = (Button) rootView.findViewById(R.id.login);
		signup = (Button) rootView.findViewById(R.id.signup);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated");
		super.onActivityCreated(savedInstanceState);
		// Login Button Click Listener
		loginbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// Retrieve the text entered from the EditText
				usernametxt = username.getText().toString();
				passwordtxt = password.getText().toString();

				// Send data to Parse.com for verification
				ParseUser.logInInBackground(usernametxt, passwordtxt,
						new LogInCallback() {
					public void done(ParseUser user, ParseException e) {
						if (user != null) {
							
							Toast.makeText(getActivity().getApplicationContext(),
									"Successfully Logged in",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(
									getActivity().getApplicationContext(),
									"No such user exist, please signup",
									Toast.LENGTH_LONG).show();
						}
					}
				});
			}
		});
		// Sign up Button Click Listener
		signup.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// Retrieve the text entered from the EditText
				usernametxt = username.getText().toString();
				passwordtxt = password.getText().toString();

				// Force user to fill up the form
				if (usernametxt.equals("") && passwordtxt.equals("")) {
					Toast.makeText(getActivity().getApplicationContext(),
							"Please complete the sign up form",
							Toast.LENGTH_LONG).show();

				} else {
					// Save new user data into Parse.com Data Storage
					ParseUser user = new ParseUser();
					user.setUsername(usernametxt);
					user.setPassword(passwordtxt);
					user.signUpInBackground(new SignUpCallback() {
						public void done(ParseException e) {
							if (e == null) {
								// Show a simple Toast message upon successful registration
								Toast.makeText(getActivity().getApplicationContext(),
										"Successfully Signed up, please log in.",
										Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(getActivity().getApplicationContext(),
										"Sign up Error", Toast.LENGTH_LONG)
										.show();
							}
						}
					});
				}

			}
		});
	}

}
