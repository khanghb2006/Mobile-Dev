package com.example.expressionedittext;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;

public class ExpressionEditText extends AppCompatEditText {

    public ExpressionEditText(Context context) {
        super(context);
        init();
    }

    public ExpressionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExpressionEditText(
            Context context,
            AttributeSet attrs,
            int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setOnFocusChangeListener((view, hasFocus) -> {

            if (!hasFocus) {
                calculateExpression();
            }
        });
    }

    private void calculateExpression() {

        if (getText() == null) {
            return;
        }

        String expression = getText().toString().trim();

        if (expression.isEmpty()) {
            return;
        }

        try {

            double result = evaluate(expression);

            if (result == (long) result) {
                setText(String.valueOf((long) result));
            } else {
                setText(String.valueOf(result));
            }

        } catch (Exception e) {

            setError("Biểu thức không hợp lệ");
        }
    }

    private double evaluate(String expression) {

        expression = expression.replaceAll("\\s+", "");

        return new Parser(expression).parse();
    }

    private static class Parser {

        private final String expression;
        private int position = 0;

        Parser(String expression) {
            this.expression = expression;
        }

        double parse() {

            double result = parseExpression();

            if (position < expression.length()) {
                throw new IllegalArgumentException();
            }

            return result;
        }

        private double parseExpression() {

            double result = parseTerm();

            while (position < expression.length()) {

                char operator = expression.charAt(position);

                if (operator == '+') {
                    position++;
                    result += parseTerm();

                } else if (operator == '-') {
                    position++;
                    result -= parseTerm();

                } else {
                    break;
                }
            }

            return result;
        }

        private double parseTerm() {

            double result = parseNumber();

            while (position < expression.length()) {

                char operator = expression.charAt(position);

                if (operator == '*') {

                    position++;
                    result *= parseNumber();

                } else if (operator == '/') {

                    position++;

                    double divisor = parseNumber();

                    if (divisor == 0) {
                        throw new IllegalArgumentException();
                    }

                    result /= divisor;

                } else {
                    break;
                }
            }

            return result;
        }

        private double parseNumber() {

            int start = position;

            while (
                    position < expression.length()
                            && (
                            Character.isDigit(expression.charAt(position))
                                    || expression.charAt(position) == '.'
                    )
            ) {
                position++;
            }

            if (start == position) {
                throw new IllegalArgumentException();
            }

            return Double.parseDouble(
                    expression.substring(start, position)
            );
        }
    }
}