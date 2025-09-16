package org.com.todolist.global.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing                      // @CreatedDate, @LastModifiedDate 활성화
public class JpaAuditingConfig {

}
