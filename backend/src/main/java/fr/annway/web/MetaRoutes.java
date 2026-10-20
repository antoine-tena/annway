package fr.annway.web;

import fr.annway.equipment.EquipmentFactory;
import fr.annway.equipment.EquipmentType;
import java.util.Arrays;
import java.util.List;

/** Santé du serveur et références de l'interface (types de matériel). */
public final class MetaRoutes {

    private MetaRoutes() {
    }

    public static List<Route> routes() {
        return List.of(
                // Publique : la sonde de santé du déploiement n'a pas de session (limitée en débit).
                Route.get("/api/health", Access.PUBLIC, call -> ApiResponse.ok(new JsonObject().put("status", "ok"))),
                // Publique : la liste des types de matériel, sans donnée de compte (limitée en débit).
                Route.get("/api/equipment-types", Access.PUBLIC, call -> ApiResponse.ok(equipmentTypes())));
    }

    /** Chaque type de matériel et son encombrement, lu sur un exemplaire de la fabrique. */
    private static List<JsonObject> equipmentTypes() {
        return Arrays.stream(EquipmentType.values())
                .map(type -> new JsonObject()
                        .put("type", type)
                        .put("bulk", EquipmentFactory.create(type, type.name()).bulk()))
                .toList();
    }
}
