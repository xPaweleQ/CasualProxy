package xyz.yooniks.cproxy;

import xyz.yooniks.cproxy.managers.ProxyManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Proxy {/*extends JFrame {*/

    //private JPanel panel;

    public Proxy() {
        /*super("CasualProxy created by yooniks");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocation(50, 50);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(500, 300);

        this.panel = new JPanel();
        setContentPane(panel);

        final JLabel label = new JLabel("Wejdz w mc na serwer: '127.0.0.1:1337'");
        label.setBounds(250, 150, 50, 25);
        label.setSize(50, 25);
        add(label);
        final JMenuBar menuBar = new JMenuBar();
        final JMenu menu = new JMenu("Plik");
        final JMenuItem menuItem = new JMenuItem("Zamknij");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(1);
            }
        });
        menu.add(menuItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);*/

        try {
            checkWWW();
        } catch (Exception ex) {
            System.exit(1);
            return;
        }

        ProxyManager.loadProxies(false, 400);
    }

    public static void main(String[] args) {
        //EventQueue.invokeLater(() -> {
        new Proxy();/*.setVisible(true);*/
        //});
        final CasualProxy proxy = new CasualProxy();
        proxy.onLoad();
    }

    private void checkWWW() throws RuntimeException {
        try {
            URL oracle = new URL("https://raw.githubusercontent.com/yooniks/proxy_license/master/license.txt");
            URLConnection yc = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equalsIgnoreCase("true")) {
                    System.out.println(
                            "\n\n\n\n\n\n\n\n\n\n\n\n" +
                                    "##################################" +
                                    "\n\n" +
                                    "Licencja poprawna!" +
                                    "\n\n" +
                                    "#########################");
                } else {
                    throw new RuntimeException("Niepoprawna licencja, zglos sie na skype: yooniksyooniks@gmail.com");
                }
            }
            in.close();
        } catch (Throwable ex) {
            throw new RuntimeException("Niepoprawna licencja, zglos sie na skype: yooniksyooniks@gmail.com");
        }
    }
}
