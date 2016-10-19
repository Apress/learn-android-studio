package com.apress.gerber.debugme;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


public class MainActivity extends Activity {

    private static final int SECONDS = 1000;//millis
    private Spinner operators;
    private TextView answerMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(this.getClass().getSimpleName(), "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        answerMessage = (TextView) findViewById(R.id.txtAnswer);
        answerMessage.setVisibility(View.INVISIBLE);
        operators = (Spinner) findViewById(R.id.spinOperator);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.operators_array, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        operators.setAdapter(adapter);
    }

    public void checkanswer(View sender) {
        //Hide the keypad then check the answer
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(findViewById(R.id.editAnswer).getWindowToken(), 0);
        checkAnswer(sender);
    }

    public void checkAnswer(View sender) {
        String givenAnswer = ((EditText) findViewById(R.id.editAnswer)).getText().toString();
        int answer = calculateAnswer((EditText) findViewById(R.id.editItem1), (EditText) findViewById(R.id.editItem2));
        final String message = "The answer is:\n" + answer;
        if(! isNumeric(givenAnswer)) {
            showAnswer(false, "Please enter only numbers!");
        } else if(Integer.parseInt(givenAnswer) == answer) {
            showAnswer(true, message);
        } else {
            showAnswer(false, message);
        }
        eventuallyHideAnswer();
    }

    private boolean isNumeric(String givenAnswer) {
        String numbers = "1234567890";
        for(int i =0; i < givenAnswer.length(); i++){
            if(!numbers.contains(givenAnswer.substring(i,i+1))){
                return false;
            }
        }
        return true;
    }

    private int calculateAnswer(EditText item1, EditText item2) {
        int number1 = Integer.parseInt(item1.getText().toString());
        int number2 = Integer.parseInt(item2.getText().toString());
        int answer = 0;
        switch(((Spinner) findViewById(R.id.spinOperator)).getSelectedItemPosition()) {
            case 0:
                answer = number1 + number2;
                break;
            case 1:
                answer = number1 - number2;
                break;
            case 2:
                answer = number1 * number2;
                break;
            case 3:
                if(number2 != 0) {
                    answer = number1 / number2;
                }
                break;
        }
        return answer;
    }

    private void showAnswer(final boolean isCorrect, final String message) {
        if (isCorrect) {
            answerMessage.setText("Correct! " + message);
            answerMessage.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            answerMessage.setText("Incorrect! " + message);
            answerMessage.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        answerMessage.setVisibility(View.VISIBLE);
    }

    private void eventuallyHideAnswer() {
        final Runnable hideAnswer = new Runnable() {
            @Override
            public void run() {
                answerMessage.setVisibility(View.INVISIBLE);
            }
        };
        answerMessage.postDelayed(hideAnswer,10 * SECONDS);
    }
}
