package com.maliki.topquizz.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.maliki.topquizz.R;
import com.maliki.topquizz.model.Question;
import com.maliki.topquizz.model.QuestionBank;

import java.util.Arrays;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String RESULT_SCORE = "RESULT_SCORE";

    private static final String BUNDLE_STATE_SCORE = "BUNDLE_STATE_SCORE";
    private static final String BUNDLE_STATE_QUESTION_COUNT = "BUNDLE_STATE_QUESTION_COUNT";
    private static final String BUNDLE_STATE_QUESTION_BANK = "BUNDLE_STATE_QUESTION_BANK";

    private static final int INITIAL_QUESTION_COUNT = 5;

    TextView mQuestionTextView;
    Button mAnswerButton1;
    Button mAnswerButton2;
    Button mAnswerButton3;
    Button mAnswerButton4;

    private int mScore;
    private int mRemainingQuestionCount;
    private QuestionBank mQuestionBank = generateQuestionBank();

    private boolean mEnableTouchEvents;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mEnableTouchEvents = true;
        mRemainingQuestionCount = 4;
        mScore = 0;

        mQuestionTextView = findViewById(R.id.game_activity_textview_question);
        mAnswerButton1 = findViewById(R.id.game_activity_button_1);
        mAnswerButton2 = findViewById(R.id.game_activity_button_2);
        mAnswerButton3 = findViewById(R.id.game_activity_button_3);
        mAnswerButton4 = findViewById(R.id.game_activity_button_4);

        // Use the same listener for the four buttons.
        // The view id value will be used to distinguish the button triggered
        mAnswerButton1.setOnClickListener(this);
        mAnswerButton2.setOnClickListener(this);
        mAnswerButton3.setOnClickListener(this);
        mAnswerButton4.setOnClickListener(this);

        if (savedInstanceState != null) {
            mScore = savedInstanceState.getInt(BUNDLE_STATE_SCORE);
            mRemainingQuestionCount = savedInstanceState.getInt(BUNDLE_STATE_QUESTION_COUNT);
            mQuestionBank = (QuestionBank) savedInstanceState.getSerializable(BUNDLE_STATE_QUESTION_BANK);
        } else {
            mScore = 0;
            mRemainingQuestionCount = INITIAL_QUESTION_COUNT;
            mQuestionBank = generateQuestionBank();
        }

        displayQuestion(mQuestionBank.getCurrentQuestion());

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(BUNDLE_STATE_SCORE, mScore);
        outState.putInt(BUNDLE_STATE_QUESTION_COUNT, mRemainingQuestionCount);
        outState.putSerializable(BUNDLE_STATE_QUESTION_BANK, mQuestionBank);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return mEnableTouchEvents && super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        int index;

        if (v == mAnswerButton1) {
            index = 0;
        } else if (v == mAnswerButton2) {
            index = 1;
        } else if (v == mAnswerButton3) {
            index = 2;
        } else if (v == mAnswerButton4) {
            index = 3;
        } else {
            throw new IllegalStateException("Unknown clicked view : " + v);
        }

        if (index == mQuestionBank.getCurrentQuestion().getAnswerIndex()) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            mScore++;
        } else {
            Toast.makeText(this, "Incorrect!", Toast.LENGTH_SHORT).show();
        }

        mEnableTouchEvents = false;

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                mEnableTouchEvents = true;

                mRemainingQuestionCount--;

                if (mRemainingQuestionCount <= 0) {
                    // No question left end the game!
                    endGame();
                } else {
                    displayQuestion(mQuestionBank.getNextQuestion());
                }
            }
        }, 2_000);
    }

    private void endGame() {
        // No question left, end the game
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String appreciation = "";
        if (mScore<3){
            appreciation="Very bad ! Could do better !";
        }else if (mScore==3){
            appreciation = "Not so bad !";
        }else if (mScore==4){
            appreciation = "Well done !";
        }else if (mScore==5){
            appreciation = "Excellent, YOU A GENIUS !";
        }

        builder.setTitle(appreciation)
                .setMessage("Your score is " + mScore)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(RESULT_SCORE, mScore);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                })
                .create()
                .show();
    }


    private void displayQuestion(final Question question) {
        // Set the text for the question text view and the four buttons
        mQuestionTextView.setText(question.getQuestion());
        mAnswerButton1.setText(question.getChoiceList().get(0));
        mAnswerButton2.setText(question.getChoiceList().get(1));
        mAnswerButton3.setText(question.getChoiceList().get(2));
        mAnswerButton4.setText(question.getChoiceList().get(3));
    }
    private QuestionBank generateQuestionBank() {
        Question question1 = new Question(
                "Who is the creator of Android?",
                Arrays.asList(
                        "Andy Rubin",
                        "Steve Wozniak",
                        "Jake Wharton",
                        "Paul Smith"
                ),
                0
        );

        Question question2 = new Question(
                "When did the first man land on the moon?",
                Arrays.asList(
                        "1958",
                        "1962",
                        "1967",
                        "1969"
                ),
                3
        );

        Question question3 = new Question(
                "What is the house number of The Simpsons?",
                Arrays.asList(
                        "42",
                        "742",
                        "666",
                        "908"
                ),
                1
        );

        Question question4 = new Question(
                "Who did the Mona Lisa paint?",
                Arrays.asList(
                        "Michelangelo",
                        "Raphael",
                        "Leonardo Da Vinci",
                        "Carravagio"
                ),
                2
        );

        Question question5 = new Question(
                "What is the country top-level domain of Belgium?",
                Arrays.asList(
                        ".bg",
                        ".bm",
                        ".bl",
                        ".be"
                ),
                3
        );

        Question question6 = new Question(
                "I wrote a book who's been a screen adaptation.My hero is a hobbit.Who am I?",
                Arrays.asList(
                        "Peter Jackson",
                        "J. K. Rowling",
                        "J. R. R. Tolkien",
                        "Youssou Ndour"
                ),
                1
        );

        Question question7 = new Question(
                "Who is the first prophet of all times ?",
                Arrays.asList(
                        "Younous (AS)",
                        "Ibrahim (AS)",
                        "Moussa (AS)",
                        "Adam (AS)"
                ),
                3
        );

        Question question8 = new Question(
                "Which color do you get when Red and Green are mixed",
                Arrays.asList(
                        "magenta",
                        "cyan",
                        "yellow",
                        "blue"
                ),
                2
        );

        Question question9 = new Question(
                "What is the result of (x-a)*(x-b)*...(x-z)",
                Arrays.asList(
                        "0",
                        "indetermined",
                        "don't know maths",
                        "bed"
                ),
                0
        );

        Question question10 = new Question(
                "What is the country top-level domain of Guinea?",
                Arrays.asList(
                        ".ng",
                        ".ge",
                        ".gi",
                        ".gn"
                ),
                3
        );

        return new QuestionBank(Arrays.asList(question1, question2, question3, question4, question5,
                question6, question7, question8, question9, question10));
    }
}