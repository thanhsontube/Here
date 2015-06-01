package son.nt.here.loader;

import org.apache.http.client.methods.HttpUriRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import son.nt.here.utils.ContentLoader;

/**
 * Created by Sonnt on 5/3/15.
 */
public abstract class NearbyPlacesLoader extends ContentLoader<String> {
    protected NearbyPlacesLoader(HttpUriRequest request, boolean useCache) {
        super(request, useCache);
    }

    @Override
    protected String handleStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            int buf = -1;
            while ((buf = br.read()) >= 0) {
                sb.append((char) buf);
            }
            return sb.toString();
        } catch (Exception e) {

            throw new IOException(e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
