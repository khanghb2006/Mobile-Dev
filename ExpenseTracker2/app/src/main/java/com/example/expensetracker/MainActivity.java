package com.example.expensetracker;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    // Category carries its own label, color and icon -> no switch needed
    private enum Category {
        FOOD("Food", 0xFF2E7D32, android.R.drawable.ic_menu_agenda),
        TRANSPORT("Transport" , 0xFF1565C0 , android.R.drawable.ic_menu_compass),
        ENTERTAINMENT("Entertainment" , 0xFFEF6C00 , android.R.drawable.ic_menu_slideshow),
        OTHER("Other" , 0xFF607D8B ,android.R.drawable.ic_menu_info_details);

        final String label;
        final int color;
        final int icon;

        Category(String label , int color , int icon) {
            this.label = label;
            this.color = color;
            this.icon = icon;
        }
    }

    private static final int MATCH = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;
    private static final int GREEN = 0xFF2E7D32;
    private static final int RED = 0xFFD32F2F;
    private static final int GREY = 0xFF757575;
    private static final int BORDER = 0xFFBDBDBD;

    private static final SimpleDateFormat FIELD_FORMAT = new SimpleDateFormat("MM/dd/yyyy" , Locale.US);
    private static final SimpleDateFormat ROW_FORMAT = new SimpleDateFormat("MMM d, yyyy" , Locale.US);

    // Widget the click handlers need to read from
    private EditText titleInput , amountInput , dateInput , notesInput;
    private Spinner categorySpinner;
    private LinearLayout expenseList;
    private Date pickedDate;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(buildRoot());
        seedSampleExpenses();
    }

    // Helper function -- Tiny view
    private LinearLayout linear(int orientation) {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(orientation);
        return l;
    }

    private TextView text (String value , int sp , int color , boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(sp);
        t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT_BOLD);
        return t;
    }

    private TextView prefix(String label) {
        return text(label , 14 , 0xFF424242 , true);
    }

    private TextView sectionTitle(String label) {
        return text(label , 16 , GREEN , true);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    // Full-width layout params with a top margin in dp
    private ViewGroup.LayoutParams lp(int height , int topMarginDp) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(MATCH , height);
        p.topMargin = dp(topMarginDp);
        return p;
    }

    // Tinted icon
    private ImageView icon (int res , int tint , int sizeDp) {
        ImageView v = new ImageView(this);
        v.setImageResource(res);
        v.setColorFilter(tint);
        v.setLayoutParams(new LinearLayout.LayoutParams(dp(sizeDp) , dp(sizeDp)));
        return v;
    }

    // Rounded rectangle with a 1dp stroke
    private GradientDrawable box(int fill , int stroke) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(fill);
        d.setCornerRadius(dp(6));
        d.setStroke(dp(1) , stroke);
        return d;
    }

    // Form helpers

    // Bordered row
    private LinearLayout fieldRow(View leading , View field , View trailing) {
        LinearLayout row = linear(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12) , dp(4) , dp(12) , dp(4));
        row.setBackground(box(Color.WHITE , BORDER));

        row.addView(leading);
        row.addView(field , new LinearLayout.LayoutParams(0 , WRAP , 1f));
        if (trailing != null) row.addView(trailing);
        return row;
    }

    private EditText input(String hint , int inputType) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setInputType(inputType);
        e.setBackground(null);
        e.setTextSize(15);
        e.setPadding(dp(12) , 0 , 0 , 0);
        return e;
    }

    // Select Item is placeholder
    private Spinner buildCategorySpinner() {
        List<String> names = new ArrayList<>();
        names.add("Select category");
        for (Category c : Category.values())
            names.add(c.label);

        Spinner spinner = new Spinner(this);
        spinner.setAdapter(new ArrayAdapter<>(this , android.R.layout.simple_spinner_dropdown_item , names));
        return spinner;
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this , (picker , year , month , day) -> {
            c.set(year, month, day);
            pickedDate = c.getTime();
            dateInput.setText(FIELD_FORMAT.format(pickedDate));
        }, c.get(Calendar.YEAR) , c.get(Calendar.MONTH) , c.get(Calendar.DAY_OF_MONTH)).show();
    }

    // Action
    private void onAddExpense() {
        double amount;
        try {
            amount = Double.parseDouble(amountInput.getText().toString());
        } catch (NumberFormatException e) {
            amountInput.setError("Enter a valid amount");
            return;
        }

        String title = titleInput.getText().toString().trim();
        int CategoryPos = categorySpinner.getSelectedItemPosition();
        if (title.isEmpty() || CategoryPos == 0 || pickedDate == null) {
            Toast.makeText(this, "Please fill all requiried files", Toast.LENGTH_SHORT).show();
            return;
        }

        Category category = Category.values()[CategoryPos - 1];
        View row = buildExpenseRow(title , ROW_FORMAT.format(pickedDate) , category , amount);
        expenseList.addView(row , 0 , lp(WRAP , 8)); // newest on the top
        clearForm();
    }

    private void clearForm() {
        titleInput.setText("");
        amountInput.setText("");
        dateInput.setText("");
        notesInput.setText("");
        categorySpinner.setSelection(0);
        pickedDate = null;
    }

    // Expense Row
    private View buildExpenseRow(String title , String dateText , Category category , double amount) {
        LinearLayout row = linear(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(12) , dp(12) , dp(12) , dp(12));
        row.setBackground(box(Color.WHITE , 0xFFE0E0E0));

        row.addView(buildBadge(category));

        LinearLayout texts = linear(LinearLayout.VERTICAL);
        texts.setPadding(dp(12), 0, 0, 0);
        texts.addView(text(title, 16, Color.BLACK, true));
        texts.addView(text(dateText + " • " + category.label, 13, GREY, false));
        row.addView(texts, new LinearLayout.LayoutParams(0, WRAP, 1f));

        TextView amountView = text(String.format(Locale.US, "$%.2f", amount), 16, RED, true);
        amountView.setPadding(0, 0, dp(8), 0);
        row.addView(amountView);
        row.addView(icon(android.R.drawable.ic_menu_more, GREY, 20));
        return row;
    }

    // Colored circle with the category icon
    private ImageView buildBadge(Category category) {
        ImageView badge = icon(category.icon, Color.WHITE, 40);
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(category.color);
        badge.setBackground(circle);
        badge.setPadding(dp(10), dp(10), dp(10), dp(10));
        return badge;
    }

    private void seedSampleExpenses() {
        expenseList.addView(buildExpenseRow("Groceries", "May 18, 2024", Category.FOOD, 45.30), lp(WRAP, 8));
        expenseList.addView(buildExpenseRow("Fuel", "May 17, 2024", Category.TRANSPORT, 35.00), lp(WRAP, 8));
        expenseList.addView(buildExpenseRow("Movie Ticket", "May 16, 2024", Category.ENTERTAINMENT, 12.50), lp(WRAP, 8));
    }

    // Components
    private View buildTopBar() {
        LinearLayout bar = linear(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(16) , 0 , dp(16) , 0);

        bar.addView(icon(android.R.drawable.ic_menu_sort_by_size , Color.WHITE , 24));

        TextView title = text("Expense Tracker" , 20 , Color.WHITE , true);
        title.setPadding(dp(24) , 0 , 0 , 0);
        bar.addView(title , new LinearLayout.LayoutParams(0 , WRAP , 1f)); // push "+" to the right

        bar.addView(icon(android.R.drawable.ic_menu_add , Color.WHITE , 24));
        return bar;
    }

    private View buildContent() {
        LinearLayout content = linear(LinearLayout.VERTICAL);
        content.setPadding(dp(16) , dp(16) , dp(16) , dp(16));

        content.addView(sectionTitle("Add New Expense"));

        titleInput = input("Enter expense title" , InputType.TYPE_CLASS_TEXT);
        content.addView(fieldRow(icon(android.R.drawable.ic_menu_edit , GREY , 20) , titleInput , null) , lp(WRAP , 8));

        amountInput = input("Enter amount", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        content.addView(fieldRow(prefix("$"), amountInput, null), lp(WRAP, 8));

        categorySpinner = buildCategorySpinner();
        content.addView(fieldRow(prefix("Category"), categorySpinner, null), lp(WRAP, 8));

        dateInput = input("MM/DD/YYYY", InputType.TYPE_NULL);
        dateInput.setFocusable(false); // read-only, picker fills it
        View dateRow = fieldRow(prefix("Date"), dateInput, icon(android.R.drawable.ic_menu_my_calendar, GREY, 20));
        dateInput.setOnClickListener(v -> showDatePicker());
        dateRow.setOnClickListener(v -> showDatePicker());
        content.addView(dateRow, lp(WRAP, 8));

        notesInput = input("Add notes", InputType.TYPE_CLASS_TEXT);
        content.addView(fieldRow(prefix("Notes (optional)"), notesInput, null), lp(WRAP, 8));

        Button addButton = new Button(this);
        addButton.setText("ADD EXPENSE");
        addButton.setTextColor(Color.WHITE);
        addButton.setBackground(box(GREEN, GREEN));
        addButton.setOnClickListener(v -> onAddExpense());
        content.addView(addButton, lp(dp(48), 12));

        content.addView(sectionTitle("Recent Expenses"), lp(WRAP, 24));
        expenseList = linear(LinearLayout.VERTICAL);
        content.addView(expenseList);
        return content;
    }

    // Screen = Top bar + Scrollable content
    private View buildRoot() {
        LinearLayout root = linear(LinearLayout.VERTICAL);
        root.setBackgroundColor(GREEN); // show behind the status bar
        root.setFitsSystemWindows(true); // keep content out of system bars
        root.addView(buildTopBar() , new LinearLayout.LayoutParams(MATCH , dp(56)));

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFFFAFAFA);
        scroll.addView(buildContent());
        root.addView(scroll , new LinearLayout.LayoutParams(MATCH , 0 , 1f));
        return root;
    }
}
