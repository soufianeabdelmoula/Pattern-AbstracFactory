package fr.vdm.referentiel.refadmin.utils;

public enum EmployeeTypeEnum {
    A("agent"),
    E("externe"),
    S("services"),
    R("ressources"),
    C("applications");

    private final String employeeType;

    EmployeeTypeEnum(String value) {
        this.employeeType = value;
    }

    public static EmployeeTypeEnum fromEmployeeTypeValue(String value) {
        for (EmployeeTypeEnum type : EmployeeTypeEnum.values()) {
            if (type.employeeType.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown employeeType value: " + value);
    }

    public String getEmployeeType() {
        return employeeType;
    }
}

