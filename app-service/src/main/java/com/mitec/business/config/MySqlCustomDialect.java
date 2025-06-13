package com.mitec.business.config;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MySqlCustomDialect extends MySQL5Dialect {

	
	public MySqlCustomDialect() {
		super();
		registerFunction("GROUP_CONCAT", 
	            new StandardSQLFunction("GROUP_CONCAT", 
	                StandardBasicTypes.STRING));
        registerFunction("COALESCE",
                new StandardSQLFunction("COALESCE",
                    StandardBasicTypes.STRING));
	}
}
