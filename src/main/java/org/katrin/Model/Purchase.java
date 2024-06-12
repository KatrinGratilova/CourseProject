package org.katrin.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {
    int id;
    int clientId;
    int convertedInnerClassId;
    double price;
    LocalDateTime checkoutDate;
}
