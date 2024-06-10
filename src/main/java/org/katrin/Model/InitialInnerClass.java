package org.katrin.Model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InitialInnerClass {
    int id;
    String name;
    int outerClassId;
    String code;
}
