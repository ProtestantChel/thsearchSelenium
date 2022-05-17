package ru.nika.thsearch;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.nika.thsearch.DAO.DataCard;
import ru.nika.thsearch.DAO.DataCardDAO;
import ru.nika.thsearch.DAO.SQLDataCardDAO;
import ru.nika.thsearch.config.PublicCard;
import ru.nika.thsearch.config.StartCheck;
import ru.nika.thsearch.selenium.MainSelenium;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;

import static java.net.NetworkInterface.getNetworkInterfaces;


@SpringBootApplication
public class ThsearchApplication {

	public static final String ICON_STR = "file:///" + System.getProperty("user.dir").replace("\\","/") +  "/depo.png";

	private static ConfigurableApplicationContext context;
	final static Logger logger = LoggerFactory.getLogger(ThsearchApplication.class);
	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				setTrayIcon();
			}
		});
		context = SpringApplication.run(ThsearchApplication.class, args);
	}

	public static  void stopSelenium(){
		try {
			String urlString = "http://" + getNetworkInterfaces().nextElement().getInetAddresses().nextElement().getHostAddress() + ":" + context.getEnvironment().getPropertySources().get("server.ports").getProperty("local.server.port") + "/start?start=false";
			HttpURLConnection connection = null;
			try {
				//Create connection
				URL url = new URL(urlString);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setRequestProperty("Content-Type",
						"application/json");
				connection.getInputStream();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		} catch (SocketException e) {
			logger.info("Не удалось получить ip адрес сервера");
		}
	}
	public static void restart() {
		ApplicationArguments args = context.getBean(ApplicationArguments.class);

		Thread thread = new Thread(() -> {
			stopSelenium();
			context.close();
			context = SpringApplication.run(ThsearchApplication.class, args.getSourceArgs());
		});

		thread.setDaemon(false);
		thread.start();
	}
	private static void setTrayIcon() {
		if(! SystemTray.isSupported() ) {
			return;
		}

		PopupMenu trayMenu = new PopupMenu();
		MenuItem item = new MenuItem("Exit");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stopSelenium();

				try {
					Thread.sleep(5000);
				} catch (InterruptedException interruptedException) {

				}
				context.close();
				System.exit(0);
			}
		});
		MenuItem item1 = new MenuItem("Restart");
		item1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				restart();
			}
		});
		MenuItem item2 = new MenuItem("Settings");
		item2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec(new String[] { "c:\\windows\\notepad.exe", "setting.properties" });
				} catch (Exception exception) {
				}
			}
		});
		trayMenu.add(item2);
		trayMenu.add(item1);
		trayMenu.add(item);


		URL imageURL = null;
		try {
			imageURL = new URL(ICON_STR);
		} catch (MalformedURLException e) {
		}
		Image icon = Toolkit.getDefaultToolkit().getImage(imageURL);
		TrayIcon trayIcon = new TrayIcon(icon, "ТН поиск", trayMenu);
		trayIcon.setImageAutoSize(true);

		SystemTray tray = SystemTray.getSystemTray();
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}

		trayIcon.displayMessage("ТН поиск", "Приложение запущено!",
				TrayIcon.MessageType.INFO);
	}

}
