package org.katrin.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Client {
    int id;
    String fullName;
    String contactData;
    String password;
}
