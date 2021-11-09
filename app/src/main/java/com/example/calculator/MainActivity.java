package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.Math;

public class MainActivity extends AppCompatActivity {

    public static double eval(final String arithmeticExpression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < arithmeticExpression.length()) ? arithmeticExpression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < arithmeticExpression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(arithmeticExpression.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = arithmeticExpression.substring(startPos, this.pos);
                    x = parseFactor();
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }

    public boolean resultCalculated = false;

    public void enterNumber(String enteredNumber, TextView calculatorText) {
        String currentText = (String) calculatorText.getText();

        int lastIndex = currentText.length() - 1;

        Pattern pattern1 = Pattern.compile("\\d");
        Matcher matcher1 = pattern1.matcher(currentText.substring(lastIndex));
        boolean isNumber = matcher1.matches();

        Pattern pattern2 = Pattern.compile("\\d| |\\.");
        Matcher matcher2 = pattern2.matcher(currentText.substring(lastIndex));
        boolean isNumberorSpaceorDot = matcher2.matches();

        if (currentText.equals("0") || (resultCalculated && isNumber)) {
            calculatorText.setText(enteredNumber);
            resultCalculated = false;
        } else if (calculatorText.length() >= 15) {
            return;
        } else if (isNumberorSpaceorDot) {
            calculatorText.setText(currentText + enteredNumber);
            resultCalculated = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView calculatorText = findViewById(R.id.calculatorText);
        MaterialButton cancelButton = findViewById(R.id.cancelButton);
        MaterialButton changeSignButton = findViewById(R.id.changeSignButton);
        MaterialButton percentageButton = findViewById(R.id.percentageButton);
        MaterialButton divisionButton = findViewById(R.id.divisionButton);
        MaterialButton sevenButton = findViewById(R.id.sevenButton);
        MaterialButton eightButton = findViewById(R.id.eightButton);
        MaterialButton nineButton = findViewById(R.id.nineButton);
        MaterialButton multiplicationButton = findViewById(R.id.multiplicationButton);
        MaterialButton fourButton = findViewById(R.id.fourButton);
        MaterialButton fiveButton = findViewById(R.id.fiveButton);
        MaterialButton sixButton = findViewById(R.id.sixButton);
        MaterialButton substractionButton = findViewById(R.id.substractionButton);
        MaterialButton oneButton = findViewById(R.id.oneButton);
        MaterialButton twoButton = findViewById(R.id.twoButton);
        MaterialButton threeButton = findViewById(R.id.threeButton);
        MaterialButton additionButton = findViewById(R.id.additionButton);
        MaterialButton zeroButton = findViewById(R.id.zeroButton);
        MaterialButton dotButton = findViewById(R.id.dotButton);
        MaterialButton equalButton = findViewById(R.id.equalButton);

        calculatorText.setText("0");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculatorText.setText("0");
            }
        });

        changeSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = (String) calculatorText.getText();

                if (currentText.contains("-")) {
                    calculatorText.setText(currentText.substring(1));
                } else if (currentText.equals("0")) {
                    return;
                } else {
                    calculatorText.setText("-" + currentText);
                }
            }
        });

        percentageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = (String) calculatorText.getText();

                int lastIndex = currentText.length() - 1;

                Pattern pattern = Pattern.compile("\\d");
                Matcher matcher = pattern.matcher(currentText.substring(lastIndex));
                boolean isNumber = matcher.matches();

                if (calculatorText.getText().length() >= 15) {
                    return;
                } else if (isNumber) {
                    calculatorText.setText(currentText + "%");
                }
            }
        });

        divisionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = (String) calculatorText.getText();

                int lastIndex = currentText.length() - 1;

                Pattern pattern = Pattern.compile("\\d|%");
                Matcher matcher = pattern.matcher(currentText.substring(lastIndex));
                boolean isPercentageorNumber = matcher.matches();

                if (calculatorText.getText().length() >= 15) {
                    return;
                } else if (isPercentageorNumber) {
                    calculatorText.setText(currentText + " / ");
                }
            }
        });

        sevenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton materialButton = (MaterialButton) v;
                String enteredNumber = materialButton.getText().toString();

                enterNumber(enteredNumber, calculatorText);
            }
        });

        eightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton materialButton = (MaterialButton) v;
                String enteredNumber = materialButton.getText().toString();

                enterNumber(enteredNumber, calculatorText);
            }
        });

        nineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton materialButton = (MaterialButton) v;
                String enteredNumber = materialButton.getText().toString();

                enterNumber(enteredNumber, calculatorText);
            }
        });

        multiplicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = (String) calculatorText.getText();

                int lastIndex = currentText.length() - 1;

                Pattern pattern = Pattern.compile("\\d|%");
                Matcher matcher = pattern.matcher(currentText.substring(lastIndex));
                boolean isPercentageorNumber = matcher.matches();

                if (currentText.length() >= 15) {
                    return;
                } else if (isPercentageorNumber) {
                    calculatorText.setText(currentText + " x ");
                }
            }
        });

        fourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton materialButton = (MaterialButton) v;
                String enteredNumber = materialButton.getText().toString();

                enterNumber(enteredNumber, calculatorText);
            }
        });

        fiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton materialButton = (MaterialButton) v;
                String enteredNumber = materialButton.getText().toString();

                enterNumber(enteredNumber, calculatorText);
            }
        });

        sixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton materialButton = (MaterialButton) v;
                String enteredNumber = materialButton.getText().toString();

                enterNumber(enteredNumber, calculatorText);
            }
        });

        substractionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = (String) calculatorText.getText();

                int lastIndex = currentText.length() - 1;

                Pattern pattern = Pattern.compile("\\d|%");
                Matcher matcher = pattern.matcher(currentText.substring(lastIndex));
                boolean isPercentageorNumber = matcher.matches();

                if (currentText.length() >= 15) {
                    return;
                } else if (isPercentageorNumber) {
                    calculatorText.setText(currentText + " - ");
                }
            }
        });

        oneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton materialButton = (MaterialButton) v;
                String enteredNumber = materialButton.getText().toString();

                enterNumber(enteredNumber, calculatorText);
            }
        });

        twoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton materialButton = (MaterialButton) v;
                String enteredNumber = materialButton.getText().toString();

                enterNumber(enteredNumber, calculatorText);
            }
        });

        threeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton materialButton = (MaterialButton) v;
                String enteredNumber = materialButton.getText().toString();

                enterNumber(enteredNumber, calculatorText);
            }
        });

        additionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = (String) calculatorText.getText();

                int lastIndex = currentText.length() - 1;

                Pattern pattern = Pattern.compile("\\d|%");
                Matcher matcher = pattern.matcher(currentText.substring(lastIndex));
                boolean isPercentageorNumber = matcher.matches();

                if (currentText.length() >= 15) {

                } else if (isPercentageorNumber) {
                    calculatorText.setText(calculatorText.getText() + " + ");
                }
            }
        });

        zeroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialButton materialButton = (MaterialButton) v;
                String enteredNumber = materialButton.getText().toString();

                enterNumber(enteredNumber, calculatorText);
            }
        });

        dotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = (String) calculatorText.getText();

                int lastIndex = currentText.length() - 1;

                if (currentText.substring(lastIndex).equals(".")) {
                    return;
                } else {
                    calculatorText.setText(currentText + ".");
                }
            }
        });

        equalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = (String) calculatorText.getText();

                String goodCurrentText = currentText.replace("x", "*");

                Pattern pattern = Pattern.compile("(\\d+) ([+\\-*\\/]) (\\d+)%");
                Matcher matcher = pattern.matcher(goodCurrentText);

                ArrayList<String> resultsArray = new ArrayList<String>();

                double result;

                while (matcher.find()) {

                    if (matcher.group(2).equals("+")) {
                        result = Double.valueOf(matcher.group(1)) + Double.valueOf(matcher.group(1)) * Double.valueOf(matcher.group(3)) / 100;
                    } else if (matcher.group(2).equals("-")) {
                        result = Double.valueOf(matcher.group(1)) - Double.valueOf(matcher.group(1)) * Double.valueOf(matcher.group(3)) / 100;
                    } else if (matcher.group(2).equals("*")) {
                        result = Double.valueOf(matcher.group(1)) * Double.valueOf(matcher.group(3)) / 100;
                    } else if (matcher.group(2).equals("/")) {
                        result = Double.valueOf(matcher.group(1)) / Double.valueOf(matcher.group(3)) / 100;
                    } else {
                        result = 0;
                    }

                    resultsArray.add(String.valueOf(result));
                }

                for (String element : resultsArray) {
                    goodCurrentText = goodCurrentText.replaceFirst("\\d+ [+\\-*\\/] \\d+%", element);
                }

                calculatorText.setText(String.valueOf(eval(goodCurrentText)));

                resultCalculated = true;
            }
        });
    }
}