package kz.smart.plaza.users.predicates;

import com.querydsl.core.types.dsl.*;
import kz.smart.plaza.users.models.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.apache.commons.lang.StringUtils.isNumeric;

@Data
@AllArgsConstructor
public class UserPredicate {
    private SearchCriteria criteria;
    public BooleanExpression getPredicate() {
        PathBuilder<User> entityPath = new PathBuilder<>(User.class, "user");
        String criteriaValue = criteria.getValue().toString();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = df.parse(criteriaValue);
        } catch (ParseException e) {
            date = null;
        }
        if (isNumeric(criteriaValue) && !criteria.getKey().equals("phone")) {
//            if (isNumeric(criteriaValue) && ((!criteria.getKey().equals("gender") || !criteria.getKey().equals("phone"))))
            NumberPath<Integer> path = entityPath.getNumber(criteria.getKey(), Integer.class);
            int value = Integer.parseInt(criteriaValue);
            switch (criteria.getOperation()) {
                case ":":
                    return path.eq(value);
                case ">":
                    return path.goe(value);
                case "<":
                    return path.loe(value);
            }
        }
        else if(date != null) {
            DateTimePath<Date> path = entityPath.getDateTime(criteria.getKey(), Date.class);
            switch (criteria.getOperation()) {
                case ">":
                    return path.after(date);
                case "<":
                    return path.before(date);
                case ":":
                    return path.eq(date);

            }
        }
        else if(criteriaValue.equalsIgnoreCase("true")||criteriaValue.equalsIgnoreCase("false")){
            BooleanPath path = entityPath.getBoolean(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase(":")) {
                return path.eq(Boolean.parseBoolean(criteriaValue));
            }
        }

        else {
            StringPath path = entityPath.getString(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase(":")) {
                return path.containsIgnoreCase(criteriaValue);
            } else if (criteria.getOperation().equalsIgnoreCase("!:")){
                return path.isNotNull();
            }
        }
        return null;
    }
}
