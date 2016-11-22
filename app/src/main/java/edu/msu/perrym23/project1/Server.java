package edu.msu.perrym23.project1;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Server {

    private static final String LOGIN_URL = "http://webdev.cse.msu.edu/~perrym23/cse476/project2/login.php";
    private static final String CREATE_USER_URL = "http://webdev.cse.msu.edu/~perrym23/cse476/project2/newuser.php";
    private static final String QUIT_GAME = "http://webdev.cse.msu.edu/~perrym23/cse476/project2/quit.php";
    private static final String UTF8 = "UTF-8";

    /**
     * Have we been told to cancel?
     */
    private boolean cancel = false;

    public void quitGame(String usr) {
        String query = QUIT_GAME + "?username=" + usr;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_OK) {
                return;
            }
        }  catch (MalformedURLException e) {
            // Should never happen
            return;
        } catch (IOException ex) {
        }
    }


    /**
     * Send the server a new username and password to create a new user
     * @param usr The new username
     * @param password The new user's password
     * @return true if the new user was successfully created
     */
    public boolean login(String usr, String password) {
        // Create the get query
        String query = LOGIN_URL + "?username=" + usr + "&password=" + password;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            if (cancel) { return false; }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();
            if(serverFailed(stream)) {
                return false;
            }

        } catch (MalformedURLException e) {
            // Should never happen
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }

    /**
     * Send the server a new username and password to create a new user
     * @param usr The new username
     * @param password The new user's password
     * @return true if the new user was successfully created
     */
    public boolean createNewUser(String usr, String password) {
        // Create the get query
        String query = CREATE_USER_URL + "?username=" + usr + "&password=" + password;

        Log.i("query: ", query);

        InputStream stream = null;
        try {
            URL url = new URL(query);

            if (cancel) { return false; }
            Log.i("response: ", "made it in");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();
            if(serverFailed(stream)) {
                return false;
            }

        } catch (MalformedURLException e) {
            // Should never happen
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if(stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }

    private boolean serverFailed(InputStream stream) {
        boolean fail = true;

        try {
            Scanner scanner = new Scanner(stream);

            String code = scanner.next();

            if (code.equals("success")) {
                fail = false;
            }

            scanner.close();

        } catch (NoSuchElementException ex) {
            fail = true;
        }

        return fail;
    }

    public void cancel() {
        this.cancel = true;
    }

    /**
     * Skip the XML parser to the end tag for whatever
     * tag we are currently within.
     * @param xml the parser
     * @throws IOException
     * @throws XmlPullParserException
     */
    public static void skipToEndTag(XmlPullParser xml)
            throws IOException, XmlPullParserException {
        int tag;
        do
        {
            tag = xml.next();
            if(tag == XmlPullParser.START_TAG) {
                // Recurse over any start tag
                skipToEndTag(xml);
            }
        } while(tag != XmlPullParser.END_TAG &&
                tag != XmlPullParser.END_DOCUMENT);
    }
}
