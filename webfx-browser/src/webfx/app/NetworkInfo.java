/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webfx.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import webfx.api.page.Adapter;
import webfx.api.page.OS;

/**
 *
 * @author attila
 */
public class NetworkInfo {
    private final Adapter context;

    public NetworkInfo(Adapter context) {
        this.context = context;
    }
    public Gateway defaultGateway;

    public NetworkInfo reload() {
        try {
            Process result = Runtime.getRuntime().exec("netstat -rn");

            BufferedReader in = new BufferedReader(new InputStreamReader(result.getInputStream()));
            for (String line; (line = in.readLine()) != null;) {
                if (line.startsWith("0.0.0.0") || line.startsWith("default"))
                    defaultGateway = parse(line, context.os);
            }
        } catch (IOException ex) {
            Logger.getLogger(NetworkInfo.class.getName()).log(Level.SEVERE, null, ex);
            defaultGateway = null;
        }
        return this;
    }
    private static final Callback<CellDataFeatures<NetworkInterface, String>, ObservableValue<String>> getName = (data) -> new SimpleStringProperty(data.getValue().getDisplayName());
    private static final Callback<CellDataFeatures<NetworkInterface, List<InterfaceAddress>>, ObservableValue<List<InterfaceAddress>>> getAddress = (data) -> new SimpleObjectProperty<>(data.getValue().getInterfaceAddresses());
    private static final Callback<CellDataFeatures<NetworkInterface, Integer>, ObservableNumberValue> getIndex = (data) -> new SimpleIntegerProperty(data.getValue().getIndex());

    public void fill(TableView table) throws SocketException {
        TableColumn colName = new TableColumn("Name");
        TableColumn colAddress = new TableColumn("IP Address");
        TableColumn colIndex = new TableColumn("Index");

        colName.setCellValueFactory(getName);
        colAddress.setCellValueFactory(getAddress);
        colIndex.setCellValueFactory(getIndex);

        table.getColumns().clear();
        table.getColumns().addAll(colName, colAddress, colIndex);

        table.getItems().clear();
        Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
        while (ifaces.hasMoreElements())
            table.getItems().add(ifaces.nextElement());
    }

    public static class Gateway {

        public final String dest, gateway, genmask, flags;
        public final NetworkInterface networkInterface;

        Gateway(String dest, String gateway, String genmask, String flags, NetworkInterface iface) {
            this.dest = dest;
            this.gateway = gateway;
            this.genmask = genmask;
            this.flags = flags;
            this.networkInterface = iface;
        }

        @Override
        public String toString() {
            return "Gateway{" + "dest=" + dest + ", gateway=" + gateway + ", genmask=" + genmask + ", flags=" + flags + '}';
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + Objects.hashCode(this.dest);
            hash = 97 * hash + Objects.hashCode(this.gateway);
            hash = 97 * hash + Objects.hashCode(this.genmask);
            hash = 97 * hash + Objects.hashCode(this.flags);
            hash = 97 * hash + Objects.hashCode(this.networkInterface);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Gateway other = (Gateway) obj;
            if (!Objects.equals(this.dest, other.dest))
                return false;
            if (!Objects.equals(this.gateway, other.gateway))
                return false;
            if (!Objects.equals(this.genmask, other.genmask))
                return false;
            if (!Objects.equals(this.flags, other.flags))
                return false;
            if (!Objects.equals(this.networkInterface, other.networkInterface))
                return false;
            return true;
        }

    }

    private static Gateway parse(String line, OS os) {
        String dst = null, gw = null, gm = null, flag = null;
        NetworkInterface iface = null;
        StringTokenizer st = new StringTokenizer(line);
        if (os.isLinux())
            for (int i = 0; st.hasMoreTokens(); i++) {
                String token = st.nextToken();
                switch (i) {
                    case 0:
                        if (token.equals("0.0.0.0"))
                            dst = "default";
                        else
                            dst = token;
                        break;
                    case 1:
                        gw = token;
                        break;
                    case 2:
                        gm = token;
                        break;
                    case 3:
                        flag = token;
                        break;
                    case 7:
                        try {
                            iface = NetworkInterface.getByName(token);
                        } catch (SocketException ex) {
                            Logger.getLogger(NetworkInfo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                }
            }
        else if (os.isMac())
            for (int i = 0; st.hasMoreTokens(); i++) {
                String token = st.nextToken();
                switch (i) {
                    case 0:
                        dst = token;
                        break;
                    case 1:
                        gw = token;
                        break;
                    case 2:
                        flag = token;
                        break;
                    case 5:
                        try {
                            iface = NetworkInterface.getByName(token);
                        } catch (SocketException ex) {
                            Logger.getLogger(NetworkInfo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                }
            }
        else
            for (int i = 0; st.hasMoreTokens(); i++) {
                String token = st.nextToken();
                switch (i) {
                    case 0:
                        gm = token;
                        break;
                    case 1:
                        dst = token;
                        break;
                    case 2:
                        gw = token;
                        break;
                    case 4:
                        try {
                            iface = NetworkInterface.getByIndex(Integer.decode(token));
                        } catch (SocketException ex) {
                            Logger.getLogger(NetworkInfo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                }
            }
        return new Gateway(dst, gw, gm, flag, iface);
    }
}
