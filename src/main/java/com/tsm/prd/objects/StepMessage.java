package com.tsm.prd.objects;

public enum StepMessage {
    DUPLICATE_ROUTES,
    DUPLICATE_MAPPINGS,
    IN_PRD_BUT_NOT_IN_BO,
    IN_BO_BUT_NOT_IN_PRD,
    NOT_IN_THE_AIRPORT_GROUP,
    WRONG_GROUP_FOR_OUTPUT_ROUTE,// departures in output routes are not from input departure airport group
    INVALID_OUT_ROUTES_IN_GROUP;// output routes contains routes but these routes are not in partner file
}
