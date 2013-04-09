import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.mule.api.MuleContext;
import org.mule.api.MuleException;
import org.mule.api.config.ConfigurationException;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.config.spring.SpringXmlConfigurationBuilder;
import org.mule.context.DefaultMuleContextFactory;

public class MuleLauncher {

    private static MuleContext muleContext = null;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread()  {
                public void run()  {
                    shutdown();
                }
            });
    }

    private static void shutdown() {
        Thread shutdownThread = new Thread()  {
                public void run()  {
                    if (muleContext != null)  {

                        MuleContext server = muleContext;
                        try  {
                            server.stop();
                        } catch (MuleException ex)  {
                            ex.printStackTrace();
                        }
                    }
                }
            };
        shutdownThread.start();
        while (shutdownThread.isAlive())  {
            try  {
                shutdownThread.join(750);
                if (shutdownThread.isAlive())  {
                    Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                    Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
                    Thread currentThread = Thread.currentThread();
                    for (Thread t : threadArray)  {
                        if (t != currentThread && t != shutdownThread)  {
                            try  {
                                t.interrupt();
                            } catch (Throwable th)  {
                            }
                        }
                    }
                }
            } catch (InterruptedException ex)  {
            }
        }
    }

    private static FilenameFilter mFlowFilenameFilter = new FilenameFilter()  {
            public boolean accept(File dir, String name)  {
                return (name.endsWith(".mflow"));
            }
        };

    private static MuleContext buildMuleContext() throws ConfigurationException, InitialisationException {
        File flowDir = new File("flows");
        ArrayList<String> muleConfigList = new ArrayList<String>();
        muleConfigList.add("mule-config.xml");

        if (flowDir.exists() && flowDir.isDirectory())  {
            String[] flowFilenames = flowDir.list(mFlowFilenameFilter);
            if (flowFilenames != null && flowFilenames.length > 0)  {
                for (int i = 0; i < flowFilenames.length; i++)  {
                    flowFilenames[i] = "flows/" + flowFilenames[i];
                }
                muleConfigList.addAll(Arrays.asList(flowFilenames));
            }
        }

        String[] muleConfigArray = muleConfigList.toArray(new String[muleConfigList.size()]);
        SpringXmlConfigurationBuilder configBuilder = new SpringXmlConfigurationBuilder(muleConfigArray);
        DefaultMuleContextFactory muleContextFactory = new DefaultMuleContextFactory();

        return muleContextFactory.createMuleContext(configBuilder);
    }

    public static void main(final String[] args) throws Exception {
        String port = "8080";
        if (args.length > 0)  {
            port = args[0];
        }
        System.getProperties().put("http.port", port);

        muleContext = buildMuleContext();
        muleContext.start();
    }
}
