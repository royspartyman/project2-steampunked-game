package edu.msu.perrym23.project2;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.NoSuchElementException;
import java.util.Scanner;

import edu.msu.perrym23.project2.GameActivity;

public class Server {

    private static final String LOGIN_URL = "https://webdev.cse.msu.edu/~perrym23/cse476/project2/login.php";
    private static final String CREATE_USER_URL = "https://webdev.cse.msu.edu/~perrym23/cse476/project2/newuser.php";
    private static final String CREATE_NEW_GAME = "https://webdev.cse.msu.edu/~perrym23/cse476/project2/newgame.php";
    private static final String JOIN_GAME = "https://webdev.cse.msu.edu/~perrym23/cse476/project2/joingame.php";
    private static final String UPDATE_GAME = "https://webdev.cse.msu.edu/~perrym23/cse476/project2/updategame.php";
    private static final String GET_GAME_STATUS = "https://webdev.cse.msu.edu/~perrym23/cse476/project2/getgamestatus.php";
    private static final String QUIT_GAME = "https://webdev.cse.msu.edu/~perrym23/cse476/project2/quit.php";
    private static final String GAME_READY = "https://webdev.cse.msu.edu/~perrym23/cse476/project2/gameready.php";
    private static final String CHANGE_TURN = "https://webdev.cse.msu.edu/~perrym23/cse476/project2/changeturn.php";
    private static final String GET_PLAYER_TWO = "https://webdev.cse.msu.edu/~perrym23/cse476/project2/getplayertwo.php";
    private static final String GET_PLAYER_ONE = "https://webdev.cse.msu.edu/~perrym23/cse476/project2/getplayerone.php";
    private static final String GET_CURRENT_PLAYER = "https://webdev.cse.msu.edu/~perrym23/cse476/project2/getcurrentplayer.php";
    private static final String UTF8 = "UTF-8";

    private boolean cancel = false;

    public enum GamePostMode {
        CREATE,
        UPDATE
    }

    public String getPlayerTwo(String usr) {
        String query = GET_PLAYER_TWO + "?username=" + usr;
        String name = "";

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                name = line;
            }
            return name;

        } catch (MalformedURLException e) {
            // Should never happen
            return "";
        } catch (IOException ex) {
            return "";
        }
    }

    public String getPlayerOne(String usr) {
        String query = GET_PLAYER_ONE + "?username=" + usr;
        String name = "";

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                name = line;
            }
            return name;

        } catch (MalformedURLException e) {
            // Should never happen
            return "";
        } catch (IOException ex) {
            return "";
        }
    }

    public String getCurrentPlayer(String usr) {
        String query = GET_CURRENT_PLAYER + "?username=" + usr;
        String playerTurn = "";

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream stream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                playerTurn = line;
            }
            return playerTurn;

        } catch (MalformedURLException e) {
            // Should never happen
            return "";
        } catch (IOException ex) {
            return "";
        }
    }

    public void quitGame(String usr) {
        String query = QUIT_GAME + "?username=" + usr;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return;
            }
        } catch (MalformedURLException e) {
            return;
        } catch (IOException ex) {
        }
    }

    public boolean changeTurn(String usr) {

        String query = CHANGE_TURN + "?username=" + usr;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            if (cancel) {
                return false;
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();
            if (serverFailed(stream)) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }

    public boolean gameReady(String usr) {
        String query = GAME_READY + "?username=" + usr;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            if (cancel) {
                return false;
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();
            if (serverFailed(stream)) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }

    public InputStream getGameState(String usr) {
        String query = GET_GAME_STATUS + "?username=" + usr;

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream stream = conn.getInputStream();
            /*BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = reader.readLine()) != null) {
                Log.i("game", line);
            }*/
            return stream;

        } catch (MalformedURLException e) {
            // Should never happen
            return null;
        } catch (IOException ex) {
            return null;
        }
    }


    public boolean sendGameState(String usr, GameActivity game, GamePostMode mode) {
        XmlSerializer xml = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try {
            xml.setOutput(writer);

            xml.startDocument(UTF8, true);

            game.saveToXML(xml);

            xml.endDocument();

        } catch (IOException e) {
            return false;
        }

        final String xmlStr = writer.toString();

        String postDataStr;
        try {
            postDataStr = "game=" + URLEncoder.encode(xmlStr, UTF8);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        //postDataStr = "game=" + xmlStr;

        byte[] postData = postDataStr.getBytes();

        InputStream stream = null;
        try {
            String query;
            switch (mode) {
                case CREATE:
                    query = CREATE_NEW_GAME + "?username=" + usr;
                    break;
                case UPDATE:
                    query = UPDATE_GAME + "?username=" + usr;
                    break;
                default:
                    query = UPDATE_GAME + "?username=" + usr;
                    break;
            }
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            OutputStream out = conn.getOutputStream();
            out.write(postData);
            out.close();

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();

            if (serverFailed(stream)) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }

    public boolean joinGame(String usr) {

        String query = JOIN_GAME + "?username=" + usr;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            if (cancel) {
                return false;
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            String postDataStr = "bullshit";
            byte[] postData = postDataStr.getBytes();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            OutputStream out = conn.getOutputStream();
            out.write(postData);
            out.close();

            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();
            if (serverFailed(stream)) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }

    public boolean login(String usr, String password) {
        String query = LOGIN_URL + "?username=" + usr + "&password=" + password;

        InputStream stream = null;
        try {
            URL url = new URL(query);

            if (cancel) {
                return false;
            }
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();
            if (serverFailed(stream)) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex) {
                    // Fail silently
                }
            }
        }

        return true;
    }

    public boolean createNewUser(String usr, String password) {
        String query = CREATE_USER_URL + "?username=" + usr + "&password=" + password;

        Log.i("query: ", query);

        InputStream stream = null;
        try {
            URL url = new URL(query);

            if (cancel) {
                return false;
            }
            Log.i("response: ", "made it in");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }

            stream = conn.getInputStream();
            if (serverFailed(stream)) {
                return false;
            }

        } catch (MalformedURLException e) {
            return false;
        } catch (IOException ex) {
            return false;
        } finally {
            if (stream != null) {
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

    public static void skipToEndTag(XmlPullParser xml)
            throws IOException, XmlPullParserException {
        int tag;
        do {
            tag = xml.next();
            if (tag == XmlPullParser.START_TAG) {
                skipToEndTag(xml);
            }
        } while (tag != XmlPullParser.END_TAG &&
                tag != XmlPullParser.END_DOCUMENT);
    }
}