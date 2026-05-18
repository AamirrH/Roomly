export const img = {
  heroLeft: "https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1100&q=88",
  heroRight: "https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1100&q=88",
  paris: "https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1000&q=88",
  bali: "https://images.unsplash.com/photo-1540541338287-41700207dee6?auto=format&fit=crop&w=1000&q=88",
  manhattan: "https://images.unsplash.com/photo-1582719508461-905c673771fd?auto=format&fit=crop&w=1000&q=88",
  searchA: "https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=1000&q=88",
  searchB: "https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?auto=format&fit=crop&w=1000&q=88",
  searchC: "https://images.unsplash.com/photo-1564501049412-61c2a3083791?auto=format&fit=crop&w=1000&q=88",
  detailHero: "https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?auto=format&fit=crop&w=1400&q=88",
  bath: "https://images.unsplash.com/photo-1584622650111-993a426fbf0a?auto=format&fit=crop&w=900&q=88",
  breakfast: "https://images.unsplash.com/photo-1551218808-94e220e084d2?auto=format&fit=crop&w=900&q=88",
  pool: "https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=900&q=88",
  roomA: "https://images.unsplash.com/photo-1591088398332-8a7791972843?auto=format&fit=crop&w=1000&q=88",
  roomB: "https://images.unsplash.com/photo-1609949279531-cf48d64bed89?auto=format&fit=crop&w=1000&q=88",
  roomC: "https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?auto=format&fit=crop&w=1000&q=88",
  avatarA: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=200&q=80",
  avatarB: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=200&q=80",
  avatarC: "https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=200&q=80"
};

export const fallbackHotels = [
  {
    id: 1,
    name: "The Ritz Roomly",
    city: "Mumbai",
    address: "Marine Drive, Mumbai",
    rating: 4.9,
    photos: [img.paris],
    amenities: ["Spa", "Pool", "Breakfast"],
    estimatedStartingPrice: 12800
  },
  {
    id: 2,
    name: "Ubud Sanctuary",
    city: "Goa",
    address: "Mandrem Beach, Goa",
    rating: 4.8,
    photos: [img.bali],
    amenities: ["Wellness", "Beach", "Dining"],
    estimatedStartingPrice: 9300
  },
  {
    id: 3,
    name: "The Loft Jaipur",
    city: "Jaipur",
    address: "Civil Lines, Jaipur",
    rating: 5,
    photos: [img.manhattan],
    amenities: ["Heritage", "Rooftop", "Valet"],
    estimatedStartingPrice: 11600
  },
  {
    id: 4,
    name: "Maison Emerald",
    city: "Delhi",
    address: "Chanakyapuri, Delhi",
    rating: 4.7,
    photos: [img.searchA],
    amenities: ["Gym", "Fine Dining", "Spa"],
    estimatedStartingPrice: 14800
  },
  {
    id: 5,
    name: "The Peninsula Roomly",
    city: "Bengaluru",
    address: "MG Road, Bengaluru",
    rating: 4.8,
    photos: [img.searchB],
    amenities: ["Business Lounge", "Pool"],
    estimatedStartingPrice: 10200
  },
  {
    id: 6,
    name: "Le Meurice Roomly",
    city: "Udaipur",
    address: "Lake Pichola, Udaipur",
    rating: 4.9,
    photos: [img.searchC],
    amenities: ["Lake View", "Butler", "Breakfast"],
    estimatedStartingPrice: 17200
  }
];

export const fallbackRooms = [
  {
    id: 11,
    hotelId: 1,
    type: "Deluxe King Room",
    capacity: 2,
    basePrice: 8200,
    estimatedTotalPrice: 24600,
    estimatedAverageNightlyPrice: 8200,
    amenities: ["Free WiFi", "Nespresso", "Marble Bath", "Garden View"],
    photos: [img.roomA]
  },
  {
    id: 12,
    hotelId: 1,
    type: "Executive Suite",
    capacity: 3,
    basePrice: 18400,
    estimatedTotalPrice: 55200,
    estimatedAverageNightlyPrice: 18400,
    amenities: ["Butler Service", "Private Lounge", "Mini Bar", "City View"],
    photos: [img.roomB]
  },
  {
    id: 13,
    hotelId: 1,
    type: "Superior Junior Suite",
    capacity: 2,
    basePrice: 12800,
    estimatedTotalPrice: 38400,
    estimatedAverageNightlyPrice: 12800,
    amenities: ["Balcony", "Rainfall Shower", "Work Desk", "Breakfast"],
    photos: [img.roomC]
  }
];

export const sampleBookings = [
  {
    id: "RM-92831",
    hotelName: "The Kanso Sanctuary",
    roomType: "Zen Garden Suite",
    city: "Kyoto",
    checkInDate: "2026-06-12",
    checkoutDate: "2026-06-18",
    status: "CONFIRMED",
    finalCalculatedPrice: 184000,
    photo: img.heroLeft
  },
  {
    id: "RM-77402",
    hotelName: "Astra Heritage Villa",
    roomType: "Cliffside Infinity Studio",
    city: "Goa",
    checkInDate: "2026-07-04",
    checkoutDate: "2026-07-07",
    status: "RESERVED",
    finalCalculatedPrice: 210000,
    photo: img.bali
  },
  {
    id: "RM-44112",
    hotelName: "The Emerald Estate",
    roomType: "Standard Room",
    city: "Delhi",
    checkInDate: "2026-04-01",
    checkoutDate: "2026-04-03",
    status: "CANCELLED",
    finalCalculatedPrice: 45000,
    photo: img.searchA
  }
];
