// Véhicule saisi : à l'inscription d'un conducteur ou à l'ajout d'un véhicule.

export interface VehicleInput {
  registration: string
  model: string
  seatCount: number
  equipmentCapacity: number
}

const DEFAULT_SEAT_COUNT = 3
const DEFAULT_EQUIPMENT_CAPACITY = 4

export function emptyVehicle(): VehicleInput {
  return { registration: '', model: '', seatCount: DEFAULT_SEAT_COUNT, equipmentCapacity: DEFAULT_EQUIPMENT_CAPACITY }
}
