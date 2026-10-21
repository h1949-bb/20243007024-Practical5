package com.example.practicalfive;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.atomic.AtomicReference;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MunicipalityFlowTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    private void enter(String input) {
        onView(withId(R.id.municipalityInput)).perform(scrollTo(), replaceText(input), pressImeActionButton());
    }

    @Test public void rejectsUnsupportedEmptyAndWhitespaceInput() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            for (String input : new String[] {"Guangzhou", "", "   "}) {
                enter(input);
                onView(withId(R.id.municipalityInput)).check(matches(isDisplayed()));
                scenario.onActivity(activity -> {
                    EditText field = activity.findViewById(R.id.municipalityInput);
                    int error = input.trim().isEmpty() ? R.string.error_empty : R.string.error_invalid;
                    assertEquals(activity.getString(error), field.getError().toString());
                });
            }
        }
    }

    @Test public void acceptsCaseVariantsAndSurroundingSpacesThroughTheKeyboard() throws InterruptedException {
        for (String input : new String[] {"Beijing", "beijing", "BEIJING", "  Beijing  ", "ChOnGqInG"}) {
            try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
                enterAndAwaitDetail(input);
                String expected = input.toLowerCase(java.util.Locale.ROOT).contains("beijing") ? "Beijing" : "Chongqing";
                onView(withId(R.id.municipalityName)).check(matches(withText(expected)));
                pressBack();
                onView(withId(R.id.municipalityInput)).check(matches(isDisplayed()));
            }
        }
    }

    @Test public void allFourCitiesShowMatchingContentColoursAndPhotographs() {
        String[] names = {"Beijing", "Shanghai", "Chongqing", "Tianjin"};
        int[] colours = {R.color.municipality_beijing, R.color.municipality_shanghai,
                R.color.municipality_chongqing, R.color.municipality_tianjin};
        int[] images = {R.drawable.beijing, R.drawable.shanghai, R.drawable.chongqing, R.drawable.tianjin};
        String[] summaries = context.getResources().getStringArray(R.array.municipalities_array);
        assertEquals(4, summaries.length);
        for (int index = 0; index < names.length; index++) {
            final int city = index;
            Intent intent = new Intent(context, Municipality.class).putExtra(Municipality.EXTRA_MUNICIPALITY, names[city]);
            try (ActivityScenario<Municipality> scenario = ActivityScenario.launch(intent)) {
                onView(withId(R.id.visitSiteButton)).check(matches(isDisplayed()));
                scenario.onActivity(activity -> {
                    TextView title = activity.findViewById(R.id.municipalityName);
                    TextView info = activity.findViewById(R.id.municipalityInfo);
                    ImageView image = activity.findViewById(R.id.municipalityImage);
                    Button button = activity.findViewById(R.id.visitSiteButton);
                    int colour = ContextCompat.getColor(activity, colours[city]);
                    assertEquals(names[city], title.getText().toString());
                    assertEquals(summaries[city], info.getText().toString());
                    int words = info.getText().toString().trim().split("\\s+").length;
                    assertTrue("Each original summary needs 80-120 words", words >= 80 && words <= 120);
                    assertEquals(colour, ((ColorDrawable) title.getBackground()).getColor());
                    assertEquals(colour, info.getCurrentTextColor());
                    assertEquals(colour, button.getBackgroundTintList().getDefaultColor());
                    assertEquals("Photo of " + names[city], image.getContentDescription().toString());
                    assertTrue(render(image.getDrawable()).sameAs(render(ContextCompat.getDrawable(activity, images[city]))));
                });
                scenario.recreate();
                onView(withId(R.id.municipalityName)).check(matches(withText(names[city])));
                onView(withId(R.id.visitSiteButton)).check(matches(isDisplayed()));
            }
        }
    }

    @Test public void websiteButtonSendsTheCorrectImplicitIntentForEveryCity() {
        String[] names = {"Beijing", "Shanghai", "Chongqing", "Tianjin"};
        String[] websites = {"https://english.beijing.gov.cn/", "https://english.shanghai.gov.cn/",
                "https://english.cq.gov.cn/", "https://en.tj.gov.cn/"};
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        AtomicReference<Intent> captured = new AtomicReference<>();
        Instrumentation.ActivityMonitor monitor = new Instrumentation.ActivityMonitor() {
            @Override public Instrumentation.ActivityResult onStartActivity(Intent intent) {
                if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                    captured.set(new Intent(intent));
                    return new Instrumentation.ActivityResult(Activity.RESULT_OK, null);
                }
                return null;
            }
        };
        instrumentation.addMonitor(monitor);
        try {
            for (int i = 0; i < names.length; i++) {
                captured.set(null);
                Intent intent = new Intent(context, Municipality.class).putExtra(Municipality.EXTRA_MUNICIPALITY, names[i]);
                try (ActivityScenario<Municipality> scenario = ActivityScenario.launch(intent)) {
                    onView(withId(R.id.visitSiteButton)).perform(click());
                    assertNotNull("Website intent should be sent", captured.get());
                    assertEquals(Intent.ACTION_VIEW, captured.get().getAction());
                    assertEquals(websites[i], captured.get().getDataString());
                    assertNull("Website navigation must be implicit", captured.get().getComponent());
                }
            }
        } finally {
            instrumentation.removeMonitor(monitor);
        }
    }

    @Test public void missingOrInvalidExtrasFinishSafely() {
        for (String input : new String[] {null, "Guangzhou", "   "}) {
            Intent intent = new Intent(context, Municipality.class);
            if (input != null) intent.putExtra(Municipality.EXTRA_MUNICIPALITY, input);
            try (ActivityScenario<Municipality> scenario = ActivityScenario.launch(intent)) {
                assertEquals(androidx.lifecycle.Lifecycle.State.DESTROYED, scenario.getState());
            }
        }
    }

    @Test public void unavailableBrowserIsHandledWithoutLeavingTheDetailScreen() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        java.util.concurrent.atomic.AtomicBoolean attempted = new java.util.concurrent.atomic.AtomicBoolean();
        Instrumentation.ActivityMonitor monitor = new Instrumentation.ActivityMonitor() {
            @Override public Instrumentation.ActivityResult onStartActivity(Intent intent) {
                if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                    attempted.set(true);
                    throw new android.content.ActivityNotFoundException("Simulated missing browser");
                }
                return null;
            }
        };
        instrumentation.addMonitor(monitor);
        try (ActivityScenario<Municipality> scenario = ActivityScenario.launch(
                new Intent(context, Municipality.class).putExtra(Municipality.EXTRA_MUNICIPALITY, "Beijing"))) {
            onView(withId(R.id.visitSiteButton)).perform(click());
            assertTrue(attempted.get());
            onView(withId(R.id.municipalityName)).check(matches(withText("Beijing")));
            onView(withId(R.id.visitSiteButton)).check(matches(isDisplayed()));
        } finally {
            instrumentation.removeMonitor(monitor);
        }
    }

    private static Bitmap render(Drawable drawable) {
        Bitmap result = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888);
        android.graphics.Rect previous = new android.graphics.Rect(drawable.getBounds());
        drawable.setBounds(0, 0, 64, 64);
        drawable.draw(new Canvas(result));
        drawable.setBounds(previous);
        return result;
    }

    private void enterAndAwaitDetail(String input) throws InterruptedException {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        java.util.concurrent.CountDownLatch resumed = new java.util.concurrent.CountDownLatch(1);
        androidx.test.runner.lifecycle.ActivityLifecycleCallback callback = (activity, stage) -> {
            if (activity instanceof Municipality && stage == androidx.test.runner.lifecycle.Stage.RESUMED) {
                resumed.countDown();
            }
        };
        instrumentation.runOnMainSync(() -> androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
                .getInstance().addLifecycleCallback(callback));
        try {
            enter(input);
            assertTrue("Detail activity should resume after submitting " + input,
                    resumed.await(5, java.util.concurrent.TimeUnit.SECONDS));
            instrumentation.waitForIdleSync();
        } finally {
            instrumentation.runOnMainSync(() -> androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
                    .getInstance().removeLifecycleCallback(callback));
        }
    }
}
