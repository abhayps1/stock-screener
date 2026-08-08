package com.aps.mapper;


import com.aps.entity.Result;
import org.json.JSONObject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public abstract class BeanMapper {

    @Mapping(target = "gsin", expression = "java(event.optString(\"gsin\", null))")
    @Mapping(target = "type", expression = "java(event.optString(\"type\", null))")
    @Mapping(target = "companyShortName", expression = "java(event.optString(\"companyShortName\", null))")
    @Mapping(target = "oldIsin", expression = "java(event.optString(\"oldIsin\", null))")
    @Mapping(target = "newIsin", expression = "java(event.optString(\"newIsin\", null))")
    @Mapping(target = "nseSymbol", expression = "java(event.optString(\"nseSymbol\", null))")
    @Mapping(target = "bseSymbol", expression = "java(event.optString(\"bseSymbol\", null))")
    @Mapping(target = "searchId", expression = "java(event.optString(\"searchId\", null))")
    @Mapping(target = "logoUrl", expression = "java(event.optString(\"logoUrl\", null))")
    @Mapping(target = "marketCap", expression = "java(event.optString(\"marketCap\", null))")
    @Mapping(target = "details", expression = "java(event.optString(\"details\", null))")
    @Mapping(target = "description", expression = "java(getCorporateEventPillValue(event, \"description\"))")
    @Mapping(target = "eventType", expression = "java(getCorporateEventPillValue(event, \"eventType\"))")
    @Mapping(target = "primaryDate", expression = "java(getCorporateEventPillDate(event))")
    @Mapping(target = "corporateEventFilter", expression = "java(event.optString(\"corporateEventFilter\", null))")
    @Mapping(target = "instrumentType", expression = "java(event.optString(\"instrumentType\", null))")
    public abstract Result mapEventToResult(JSONObject event);

    protected String getCorporateEventPillValue(JSONObject event, String field) {
        JSONObject pill = event.optJSONObject("corporateEventPillDto");
        return pill != null ? pill.optString(field, null) : null;
    }

    protected LocalDate getCorporateEventPillDate(JSONObject event) {
        JSONObject pill = event.optJSONObject("corporateEventPillDto");
        if (pill == null) {
            return null;
        }

        String primaryDate = pill.optString("primaryDate", null);
        return (primaryDate != null && !primaryDate.isBlank()) ? LocalDate.parse(primaryDate) : null;
    }

}
