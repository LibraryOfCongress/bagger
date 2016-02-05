package gov.loc.repository.bagger.domain;

import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;

public class BaggerValidationRulesSource extends DefaultRulesSource {

  public BaggerValidationRulesSource() {
    super();
  }

  public void init() {
    clear();
  }

  public void clear() {
    java.util.List<Rules> empty = new java.util.ArrayList<Rules>();
    setRules(empty);
  }

}
