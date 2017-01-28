### Sensitive Id decomposition 

The creation of an id can be challenging especially if the true semantic id is protected by law, which is the case for people. Therefore a either a UUID is suggested or a semi-semantic approach like firstName-middleName-sirName-dayInMonth-MonthInYear-Sequence, that allows for a human readable - yet not revealing id for a person.

Other suggested methods for doing has been to create a hash(sensitive semantic key), which might work but will be vulnerable for a brute force reengineering effort. The response to that is often to salt it, that is salt(hash(sensitive sementic key)), and that is ok but is seems merely to be a very difficult way to create a UUID, which means we have a key that is developer unfriendly - at least compared to the more human readable key consisting of recognizable fragments from the real world.

The suggested approach is firstname-middlename-familyname-ddMM-sequencenumber

#### Example:

- `hans-p-hansen-0112` the initial created Hans P Hansen born on the 1st of December
- `hans-p-hansen-0112-1` the second created Hans P Hansen born on the 1st of December
- `hans-p-hansen-0112-94` the 95th created Hans P Hansen born on the 1st of December
- `mike-hansson-0309` the initially created Mike Hansson born on the 3rd of September