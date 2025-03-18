package com.example.prototipo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDaySelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {
    private MaterialCalendarView calendarView;
    private TextView eventDetails;
    private DatabaseReference eventsRef;
    private List<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        // Initialize Firebase
        eventsRef = FirebaseDatabase.getInstance().getReference("events");
        events = new ArrayList<>();

        // Initialize views
        calendarView = findViewById(R.id.calendarView);
        eventDetails = findViewById(R.id.eventDetails);

        // Setup calendar
        setupCalendar();

        // Setup bottom navigation
        setupBottomNavigation();

        // Load events from Firebase
        loadEvents();
    }

    private void setupCalendar() {
        calendarView.setOnDaySelectedListener((widget, day, selected) -> {
            showEventsForDay(day);
        });

        // Set minimum and maximum dates (e.g., current month)
        Calendar calendar = Calendar.getInstance();
        calendarView.setSelectedDate(CalendarDay.today());
        calendarView.state().edit()
                .setMinimumDate(CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1))
                .setMaximumDate(CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 0))
                .commit();
    }

    private void loadEvents() {
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        events.add(event);
                    }
                }
                updateCalendarDecorators();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CalendarActivity.this, "Error loading events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCalendarDecorators() {
        // Clear existing decorators
        calendarView.removeDecorators();

        // Add decorators for days with events
        for (Event event : events) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(event.getDate());
            CalendarDay day = CalendarDay.from(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            calendarView.addDecorator(new EventDecorator(day));
        }
    }

    private void showEventsForDay(CalendarDay day) {
        StringBuilder details = new StringBuilder();
        boolean hasEvents = false;

        for (Event event : events) {
            Calendar eventCalendar = Calendar.getInstance();
            eventCalendar.setTimeInMillis(event.getDate());
            if (eventCalendar.get(Calendar.YEAR) == day.getYear() &&
                    eventCalendar.get(Calendar.MONTH) == day.getMonth() &&
                    eventCalendar.get(Calendar.DAY_OF_MONTH) == day.getDay()) {
                details.append(event.getTitle()).append("\n");
                details.append(event.getDescription()).append("\n\n");
                hasEvents = true;
            }
        }

        if (hasEvents) {
            eventDetails.setText(details.toString());
            eventDetails.setVisibility(View.VISIBLE);
        } else {
            eventDetails.setText("No events for this day");
            eventDetails.setVisibility(View.VISIBLE);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_cal);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_search) {
                startActivity(new Intent(getApplicationContext(), SearchActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_cal) {
                return true;
            } else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }
}