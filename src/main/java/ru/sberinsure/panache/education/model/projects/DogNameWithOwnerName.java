package ru.sberinsure.panache.education.model.projects;

import io.quarkus.hibernate.orm.panache.common.ProjectedFieldName;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record DogNameWithOwnerName(String name, String ownerName) {

    /**
     * С помощью @ProjectedFieldName("owner.name") мы говорим, что ownerName нужно вытащить из PersonActiveRecordPattern таблицы
     *
     * @param name
     * @param ownerName
     */
    public DogNameWithOwnerName(String name, @ProjectedFieldName("owner.name") String ownerName) {
        this.name = name;
        this.ownerName = ownerName;
    }
}
