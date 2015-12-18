package models;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Provides CRUD functionality for accessing people. Spring Data auto-magically takes care of many standard
 * operations here.
 */
@Named
@Singleton
public interface PersonRepository extends ElasticsearchRepository<Person, String> {
}