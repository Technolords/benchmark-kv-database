package net.technolords.benchmark.database;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.kv.GetResponse;
import com.coreos.jetcd.kv.PutResponse;

public class EtcdMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(EtcdMain.class);
    private static final String ETCD_ENDPOINT = "http://192.168.106.11:2379";

    public void configureAndRun() throws ExecutionException, InterruptedException {
        LOGGER.info("About to run the Etcd client...");
        Client client = Client.builder().endpoints(ETCD_ENDPOINT).build();
        KV etcdClient = client.getKVClient();

        final String KEY = "foo";
        final String VALUE_1 = "bar1";
        final String VALUE_2 = "bar2";
        ByteSequence key = ByteSequence.fromString(KEY);
        ByteSequence value = ByteSequence.fromString(VALUE_1);

        // Store key/value
        PutResponse putResponse = etcdClient.put(key, value).get();
        LOGGER.info("Put key: {}, and value: {} -> had previous value: {}", key, value, putResponse.hasPrevKv());
        value = ByteSequence.fromString(VALUE_2);
        putResponse = etcdClient.put(key, value).get();
        LOGGER.info("Put key: {}, and value: {} -> had previous value: {}", key, value, putResponse.hasPrevKv());

        // Get completable future and response
        CompletableFuture<GetResponse> getFuture = etcdClient.get(key);
        GetResponse getResponse = getFuture.get();
        LOGGER.info("Get key: {} -> value list size: {}", key, getResponse.getKvs().size());
        for (KeyValue keyValue : getResponse.getKvs()) {
            LOGGER.info("Value: {}", keyValue.getValue());
        }

    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EtcdMain etcdMain = new EtcdMain();
        etcdMain.configureAndRun();
    }
}
