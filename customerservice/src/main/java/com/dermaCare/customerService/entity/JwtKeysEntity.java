package com.dermaCare.customerService.entity;

import java.util.Set;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Keys")
public class JwtKeysEntity {
	
	private String id;
	private String keyName;
	private Set<String> keys;

}
