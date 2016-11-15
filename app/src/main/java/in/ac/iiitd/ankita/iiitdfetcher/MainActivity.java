package in.ac.iiitd.ankita.iiitdfetcher;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Document;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";

    private TextView mTextViewResult = null;
    private EditText mEditTextURL = null;
    private Button mGetURLContentButton = null;
    private Button mResetContentButton = null;

    String url = null;

    private FetchItemsTask result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Inside Oncreate: ");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mTextViewResult = (TextView) findViewById(R.id.textViewResult);

        mEditTextURL = (EditText) findViewById(R.id.editTextURL);
        //url = String.valueOf(mEditTextURL.getText());

        mGetURLContentButton = (Button) findViewById(R.id.getURLContentButton);
        mGetURLContentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG, "Inside Oncreate: Download Button Clicked");
                //mEditTextURL = (EditText) view.findViewById(R.id.editTextURL);
                url = mEditTextURL.getText().toString();
                try {
                    result = (FetchItemsTask) new FetchItemsTask().execute(url);
                }catch (Exception e){
                    Log.i(TAG, "Inside Oncreate: Download Exception");
                    mTextViewResult.setText("Download Exception! \nDouble check your url!");
                }
            }
        });

        mResetContentButton = (Button) findViewById(R.id.ResetContent);
        mResetContentButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG, "Inside Oncreate: Reset Button Clicked");
                mTextViewResult.setText("Your result will be here!");
            }
        });
    }

    private class FetchItemsTask extends AsyncTask<String,Integer,String> {
        boolean flagSuccess = true;
        @Override
        protected String doInBackground(String... urls) {
            Log.i(TAG, "Inside doInBackground");
            String res = "";
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                try {
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String buf = "";
                    while ((buf = buffer.readLine()) != null) {
                        res += buf;
                        System.out.println(buf);
                    }
                    flagSuccess = true;

                } catch (Exception e) {
                    Log.i(TAG, "Inside Oncreate: Download Exception in background");
                    //noinspection WrongThread
                    //mTextViewResult.setText("Error");
                    flagSuccess = false;
                    e.printStackTrace();
                }
            }
            return res;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "Inside onPostExecute");
            String res = "Fetched Media: \n";
            if(flagSuccess) {
                org.jsoup.nodes.Document doc = Jsoup.parse(result.toString());
                Elements links = doc.select("a[href]");
                Elements media = doc.select("[src]");
                for (Element src : media) {
                    if (src.tagName().equals("img"))
                        res = res + src.tagName() + src.attr("abs:src")+"\n";
                }
                res = res + "\n Fetched Links: \n";
                for (Element link : links) {
                    res = res + link.attr("abs:href")+"\n";
                    res = res.trim();
                    res = res + "\n";
                }
                    mTextViewResult.setText(res);
            }else {
                mTextViewResult.setText("Download Exception! \nCheck your internet and try again!");
            }
        }
    }

}
