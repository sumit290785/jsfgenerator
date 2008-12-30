package jsfgenerator.ejb.utilities;

import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import jsfgenerator.ejb.sessionbeans.PersistenceContext;

/**
 * Lookup utility class to retrive persistence context
 * 
 * @author zoltan verebes
 * 
 */
public final class JndiLookupUtility {

	private static JndiLookupUtility instance;

	private static PersistenceContext persistenceContext;

	// avoid multiple instantiations
	private JndiLookupUtility() {

	}

	/**
	 * singleton class instance getter
	 * 
	 * @return the application scope instance of the class
	 */
	public static JndiLookupUtility getInstance() {
		if (instance == null) {
			instance = new JndiLookupUtility();
		}

		return instance;
	}

	public PersistenceContext getPersistenceContext() {
		if (persistenceContext == null) {
			Context jndiContext;
			try {
				jndiContext = getJndiContext();
				Object ref = jndiContext.lookup(getEarName() + "PersistenceContextBean/local");
				persistenceContext = (PersistenceContext) PortableRemoteObject.narrow(ref, PersistenceContext.class);
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}

		return persistenceContext;
	}

	private InitialContext getJndiContext() throws NamingException {
		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		properties.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
		properties.put(Context.PROVIDER_URL, "jnp://localhost:1099");

		return new InitialContext(properties);
	}

	private static String getEarName() {
		URL url = Thread.currentThread().getContextClassLoader().getResource("");
		Pattern p = Pattern.compile("(?i)/([^\\\\/:\\*\\?<>\\|]+)\\.ear/");
		Matcher m = p.matcher(url.getPath());
		if (m.find()) {
			return m.group(1) + "/";
		} else {
			return "";
		}
	}

}
