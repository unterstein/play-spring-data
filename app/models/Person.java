package models;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.annotation.Id;

@Document(indexName = "World")
public class Person {

  @Id
  public Long id;

  public String firstname;

  public String surname;
}
