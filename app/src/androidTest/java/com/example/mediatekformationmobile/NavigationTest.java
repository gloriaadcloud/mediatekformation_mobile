package com.example.mediatekformationmobile;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.mediatekformationmobile.vue.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import androidx.test.espresso.contrib.RecyclerViewActions;
@RunWith(AndroidJUnit4.class)
public class NavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
        new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testNavigationVersFormationsEtDetailEtVideo() throws InterruptedException {
        // Clique sur le bouton "Formations" dans MainActivity
        onView(withId(R.id.btnFormations)).perform(ViewActions.click());

        // Attente (si besoin) pour chargement de la liste
        Thread.sleep(4000); // à remplacer par IdlingResource idéalement

        // Vérifie qu'on est dans FormationsActivity via présence d'un item (ex: bouton Filtrer)
        onView(withId(R.id.btnFiltrer)).check(matches(isDisplayed()));

        onView(withId(R.id.lstFormations))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, ViewActions.click()));

        // Vérifie qu'on est bien dans UneFormationActivity
        onView(withId(R.id.txtTitle)).check(matches(isDisplayed()));

        // Clique sur le bouton image pour lancer la vidéo
        onView(withId(R.id.btnPicture)).perform(ViewActions.click());

        // Vérifie qu'on est bien dans VideoActivity (ex : WebView affichée)
        onView(withId(R.id.wbvYoutube)).check(matches(isDisplayed()));

        // Retour à l'écran précédent (UneFormationActivity)
        pressBack();

        // Vérifier qu'on est revenu à UneFormationActivity
        onView(withId(R.id.txtTitle)).check(matches(isDisplayed()));

        // Retour à l'écran FormationsActivity
        pressBack();

        // Vérifier qu'on est revenu à FormationsActivity
        onView(withId(R.id.btnFiltrer)).check(matches(isDisplayed()));

        // Retour à MainActivity
        pressBack();

        // Vérifier qu'on est revenu à MainActivity (en vérifiant la présence du bouton Formations)
        onView(withId(R.id.btnFormations)).check(matches(isDisplayed()));

        // Cliquer sur le bouton Favoris
        onView(withId(R.id.btnFavoris)).perform(ViewActions.click());
    }
}
