package ru.vershinin;

import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.EventListener;


/**
 * The Camunda Showcase Spring Boot application.
 */
@SpringBootApplication

public class Application {
  /** This class' logger. */
  private static final Logger LOG = LoggerFactory.getLogger(Application.class);

  /**
   * Post deployment work.
   * @param event
   */
  @EventListener
  public void onPostDeploy(PostDeployEvent event) {
    LOG.info("========================================");
    LOG.info("Successfully started Camunda Showcase");
    LOG.info("========================================");
  }

  /**
   * Starts this application.
   * @param args arguments
   */

  public static void main(String... args) {
    SpringApplication.run(Application.class, args);
  }

}