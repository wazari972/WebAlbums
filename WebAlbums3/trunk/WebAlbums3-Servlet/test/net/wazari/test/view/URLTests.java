/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.wazari.test.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author kevin
 */
public class URLTests {

    public URLTests() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws MalformedURLException, IOException {
        read(new URL("http://localhost:8080/WebAlbums3-Servlet/Users?userName=Kevin&action=LOGIN").openConnection());
        read(new URL("http://localhost:8080/WebAlbums3-Servlet/Choix?themeId=4").openConnection());


    }

    @After
    public void tearDown() {
    }

    @Test
    public void choix() {}

    private static String read(URLConnection yc) throws IOException {
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));
        StringBuilder sb = new StringBuilder(100) ;
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            sb.append(inputLine) ;

        in.close();
        return sb.toString() ;
    }
}