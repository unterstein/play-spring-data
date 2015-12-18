import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import play.Application;
import play.GlobalSettings;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

/**
 * Application wide behaviour. We establish a Spring application context for the dependency injection system and
 * configure Spring Data.
 */
public class Global extends GlobalSettings {

  /**
   * Declare the application context to be used - a Java annotation based application context requiring no XML.
   */
  final private AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

  /**
   * Sync the context lifecycle with Play's.
   */
  @Override
  public void onStart(final Application app) {
    super.onStart(app);

    // AnnotationConfigApplicationContext can only be refreshed once, but we do it here even though this method
    // can be called multiple times. The reason for doing during startup is so that the Play configuration is
    // entirely available to this application context.
    ctx.register(EmbeddedElasticConfig.class);
    ctx.scan("controllers", "models");
    ctx.refresh();

    // This will construct the beans and call any construction lifecycle methods e.g. @PostConstruct
    ctx.start();
  }

  /**
   * Sync the context lifecycle with Play's.
   */
  @Override
  public void onStop(final Application app) {
    // This will call any destruction lifecycle methods and then release the beans e.g. @PreDestroy
    ctx.close();

    super.onStop(app);
  }

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  @Override
  public <A> A getControllerInstance(Class<A> aClass) {
    return ctx.getBean(aClass);
  }

  @Configuration
  @EnableElasticsearchRepositories(basePackages = "models")
  public static class EmbeddedElasticConfig {

    private Settings elasticsearchSettings = ImmutableSettings.settingsBuilder()
        .put("path.home", "target/elastic")
        .put("http.port", 8200)
        .build();

    private Node node = nodeBuilder().local(true).settings(elasticsearchSettings).node();

    private Client client = node.client();

    public EmbeddedElasticConfig() {
    }

    @Bean
    public ElasticsearchTemplate elasticsearchTemplate() throws Exception {
      return new ElasticsearchTemplate(client);
    }

    @Bean
    public Client client() {
      return client;
    }

    public void destroy() throws Exception {
      node.close();
      client.close();
    }

  }
}
