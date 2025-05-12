package com.example.mediatekformationmobile;

import static org.junit.Assert.assertEquals;

import com.example.mediatekformationmobile.outils.MesOutils;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;


public class DateUtilsTest {
    @Test
    public void testConvertDateToString() {
        // Crée une date : 8 mai 2025
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.MAY, 8);  // Mois commence à 0 (JANVIER = 0)
        Date testDate = cal.getTime();

        // Appelle la méthode à tester
        String result = MesOutils.convertDateToString(testDate);

        // Vérifie que le résultat est correct
        assertEquals("08/05/2025", result);
    }
}
