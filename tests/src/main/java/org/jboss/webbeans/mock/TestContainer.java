package org.jboss.webbeans.mock;

import java.net.URL;

import org.jboss.webbeans.BeanManagerImpl;

/**
 * Control of the container, used for tests. Wraps up common operations.
 * 
 * @author pmuir
 *
 */
public class TestContainer<L extends MockServletLifecycle>
{
   
   public static class Status
   {
      
      private final Exception deploymentException;

      public Status(Exception deploymentException)
      {
         this.deploymentException = deploymentException;
      }
      
      public Exception getDeploymentException()
      {
         return deploymentException;
      }
      
      public boolean isSuccess()
      {
         return deploymentException == null;
      }
      
   }
   
   private final L lifecycle;
   private final Iterable<Class<?>> classes;
   private final Iterable<URL> beansXml;

   /**
    * Create a container, specifying the classes and beans.xml to deploy
    * 
    * @param lifecycle
    * @param classes
    * @param beansXml
    */
   public TestContainer(L lifecycle, Iterable<Class<?>> classes, Iterable<URL> beansXml)
   {
      this.lifecycle = lifecycle;
      this.classes = classes;
      this.beansXml = beansXml;
   }
   
   /**
    * Start the container, returning the container state
    * 
    * @return
    */
   public Status startContainerAndReturnStatus()
   {
      try
      {
         startContainer();
      }
      catch (Exception e) 
      {
         return new Status(e);
      }
      return new Status(null);
   }
   
   public void startContainer()
   {
      MockBeanDeploymentArchive archive = lifecycle.getDeployment().getArchive();
      archive.setBeanClasses(classes);
      if (beansXml != null)
      {
         archive.setWebBeansXmlFiles(beansXml);
      }
      lifecycle.initialize();
      lifecycle.beginApplication();
   }
   
   /**
    * Get the context lifecycle, allowing fine control over the contexts' state
    * 
    * @return
    */
   public L getLifecycle()
   {
      return lifecycle;
   }
   
   public BeanManagerImpl getBeanManager()
   {
      return lifecycle.getBootstrap().getManager(getDeployment().getArchive());
   }
   
   public MockDeployment getDeployment()
   {
      return lifecycle.getDeployment();
   }
   
   /**
    * Utility method which ensures a request is active and available for use
    * 
    */
   public void ensureRequestActive()
   {
      if (!lifecycle.isSessionActive())
      {
         lifecycle.beginSession();
      }
      if (!lifecycle.isRequestActive())
      {
         lifecycle.beginRequest();
      }
   }

   /**
    * Clean up the container, ending any active contexts
    * 
    */
   public void stopContainer()
   {
      if (lifecycle.isRequestActive())
      {
         lifecycle.endRequest();
      }
      if (lifecycle.isSessionActive())
      {
         lifecycle.endSession();
      }
      if (lifecycle.isApplicationActive())
      {
         lifecycle.endApplication();
      }
   }

}