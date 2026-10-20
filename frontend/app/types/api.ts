// Réponses de l'API Java (backend/, paquet fr.annway.web), écrites à la main faute de contrat
// OpenAPI. Montants en centimes ([MONEY]), instants en UTC ISO-8601.

export type Role = 'PASSENGER' | 'DRIVER'
export type TripType = 'STATION' | 'POLYTECH'
export type TripStatus = 'OPEN' | 'FULL' | 'FINISHED' | 'CANCELLED'
export type ReservationStatus = 'PENDING' | 'VALIDATED' | 'REFUSED' | 'CANCELLED' | 'TRIP_CANCELLED'
export type EquipmentType = 'SKI' | 'SNOWBOARD' | 'SLED'
export type PaymentType = 'FIXED_PRICE' | 'DONATION'

export interface ApiErrorBody {
  code: string
  message: string
  field: string | null
}

export interface UserSummary {
  id: number
  firstName: string
  role: Role
  averageRating: number | null
  reviewCount: number
  isVerified: boolean
  bio: string
}

export interface Vehicle {
  registration: string
  model: string
  seatCount: number
  equipmentCapacity: number
}

export interface Review {
  index: number
  author: { id: number, firstName: string }
  rating: number
  comment: string
  date: string
  reply: string | null
}

export interface PublicProfile extends UserSummary {
  vehicles: Vehicle[]
  reviews: Review[]
}

export interface Me extends PublicProfile {
  lastName: string
  email: string
  phone: string
  photoUrl: string | null
  verifications: { email: boolean, phone: boolean, identityDocument: boolean }
}

export interface Trip {
  id: number
  type: TripType
  status: TripStatus
  departure: string
  arrival: string
  departureAt: string
  availableSeats: number
  seatCount: number
  remainingEquipmentCapacity: number
  isPaymentRequired: boolean
  pricePerSeatCents: number
  driver: UserSummary
  servedStation?: string
  busDepartureAt?: string
}

export interface TripDetail extends Trip {
  vehicleModel: string
  stops: { place: string, passingAt: string }[]
}

export interface TripSearchResult {
  trips: Trip[]
  places: { departures: string[], arrivals: string[] }
}

export interface Equipment {
  type: EquipmentType
  description: string
  bulk: number
}

export interface Reservation {
  id: number
  trip: Trip
  passenger: UserSummary
  seatCount: number
  equipment: Equipment[]
  requestedAt: string
  status: ReservationStatus
  amountDueCents: number
  payment: { amountCents: number, type: PaymentType, paidAt: string } | null
  refund: { amountCents: number, refundedAt: string } | null
  refundDueCents: number
}

export interface SearchCriteria {
  departure: string
  arrival: string
  date: string
  passengerCount: number
  type: TripType | null
}

export interface TripAlert {
  index: number
  isActive: boolean
  criteria: SearchCriteria
  matchedTrips: Trip[]
}

export interface TripRequest {
  passenger: UserSummary
  criteria: SearchCriteria
  createdOn: string
}

export interface TripMessage {
  sender: UserSummary
  content: string
  sentAt: string
}

export interface EquipmentTypeInfo {
  type: EquipmentType
  bulk: number
}

export type VerificationChannel = 'email' | 'phone'

/** Aucun envoi d'e-mail ni de SMS n'est branché : l'API renvoie le code, pour la démo. */
export interface VerificationCodeSent {
  demoCode: string
}
