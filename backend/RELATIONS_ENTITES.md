# Relations entre les entites (Nancy Immo)

Ce document explique les relations JPA implementees dans le projet.

## 1) Property -> Building

### Regle metier
Une property appartient a un building.

### Cardinalite
- Plusieurs properties peuvent appartenir a un seul building.
- C'est donc une relation Many-to-One depuis Property vers Building.

### Mapping JPA
Dans PropertyModel:
```java
@ManyToOne
@JoinColumn(name = "building_id")
private BuildingModel building;
```

Dans BuildingModel (cote inverse):
```java
@OneToMany(mappedBy = "building")
private List<PropertyModel> properties;
```

### Impact base de donnees
- Colonne de cle etrangere: `building_id`
- Table: `properties`
- Cette colonne pointe vers l'id de `building`.

## 2) Property -> Landlord

### Regle metier
Une property appartient a un landlord.

### Cardinalite
- Un landlord peut posseder plusieurs properties.
- C'est une relation Many-to-One depuis Property vers Landlord.

### Mapping JPA
Dans PropertyModel:
```java
@ManyToOne
@JoinColumn(name = "landlord_id")
private LandlordModel landlord;
```

Dans LandlordModel (cote inverse):
```java
@OneToMany(mappedBy = "landlord")
private List<PropertyModel> properties;
```

### Impact base de donnees
- Colonne de cle etrangere: `landlord_id`
- Table: `properties`
- Cette colonne pointe vers l'id de `landlords`.

## 3) Lease -> Property

### Regle metier
Un lease concerne une property.

### Cardinalite actuelle
- Relation One-to-One (1 lease pour 1 property, et inversement).

### Mapping JPA
Dans LeaseModel (cote proprietaire):
```java
@OneToOne
@JoinColumn(name = "property_id", unique = true)
private PropertyModel property;
```

Dans PropertyModel (cote inverse):
```java
@OneToOne(mappedBy = "property")
private LeaseModel lease;
```

### Impact base de donnees
- Colonne de cle etrangere: `property_id`
- Table: `leases`
- `unique = true` impose qu'une property ne soit liee qu'a un seul lease.

## 4) Lease -> Tenant

### Regle metier
Un lease concerne un tenant.

### Cardinalite actuelle
- Relation One-to-One (1 lease pour 1 tenant, et inversement).

### Mapping JPA
Dans LeaseModel (cote proprietaire):
```java
@OneToOne
@JoinColumn(name = "tenant_id", unique = true)
private TenantModel tenant;
```

Dans TenantModel (cote inverse):
```java
@OneToOne(mappedBy = "tenant")
private LeaseModel lease;
```

### Impact base de donnees
- Colonne de cle etrangere: `tenant_id`
- Table: `leases`
- `unique = true` impose qu'un tenant ne soit lie qu'a un seul lease.

## Resume global

- `Property` porte les cles etrangeres vers `Building` et `Landlord`.
- `Lease` porte les cles etrangeres vers `Property` et `Tenant`.
- Les cotes avec `mappedBy` sont les cotes inverses (ils ne portent pas la cle etrangere).

## Schema logique simplifie

- Building (1) -> (N) Property
- Landlord (1) -> (N) Property
- Property (1) -> (1) Lease
- Tenant (1) -> (1) Lease

## Remarque metier importante

Si tu veux gerer l'historique locatif (plusieurs leases successifs pour une meme property ou un meme tenant), il faudra probablement passer ces 2 relations en One-to-Many / Many-to-One au lieu de One-to-One.
