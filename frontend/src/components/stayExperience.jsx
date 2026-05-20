import React from "react";
import {
  BedDouble,
  CalendarDays,
  ChevronLeft,
  MapPin,
  ShieldCheck,
  Sparkles,
  Star,
  UserRound,
  Users,
  Wifi,
  X
} from "lucide-react";
import { img } from "../data/roomlyContent";
import { firstPhoto, money, nights } from "../lib/display";

export function HotelDetail({ hotel, rooms, query, navigate, selectRoom, bookingBlocked }) {
  const amenities = ["Luxury Spa", "Michelin Dining", "Indoor Pool", "Valet Parking", "Private Gym"];
  return (
    <main className="detail-page">
      <button className="back-link" onClick={() => navigate("hotels")}><ChevronLeft size={18} /> Hotels</button>
      <header className="detail-header">
        <div>
          <h1>{hotel?.name || `Roomly ${hotel?.city}`}</h1>
          <p><MapPin size={15} /> {hotel?.address || hotel?.city}</p>
        </div>
        <div className="detail-rating">
          <span>Exceptional</span>
          <div>{Array.from({ length: 5 }).map((_, i) => <Star key={i} size={18} fill="currentColor" />)}</div>
        </div>
      </header>
      <section className="photo-mosaic">
        <img className="main" src={firstPhoto(hotel, img.detailHero)} alt="Hotel suite" />
        <img src={img.bath} alt="Hotel bath" />
        <div>
          <img src={img.breakfast} alt="Breakfast tray" />
          <div className="more-photos">
            <img src={img.pool} alt="Indoor pool" />
            <span>+12 Photos</span>
          </div>
        </div>
      </section>
      <div className="amenity-row">
        {amenities.map((amenity) => <span key={amenity}><Sparkles size={16} /> {amenity}</span>)}
      </div>
      <section className="detail-layout">
        <div className="room-selection">
          <h2>Available <em>Accommodations</em></h2>
          {bookingBlocked && (
            <div className="role-callout">
              <ShieldCheck size={18} />
              <span>Manager sessions can inspect rooms, but reservations require a separate guest account.</span>
            </div>
          )}
          {rooms.map((room, index) => <RoomSelectionCard key={room.id} room={room} featured={index === 1} onSelect={selectRoom} disabled={bookingBlocked} />)}
        </div>
        <aside className="reservation-summary">
          <h3>Reservation Summary</h3>
          <div>
            <span>Stay Dates</span>
            <strong>{query.checkInDate} - {query.checkOutDate}</strong>
          </div>
          <div>
            <span>Room Selected</span>
            <strong>Not selected yet</strong>
          </div>
          <div className="summary-total">
            <span>Estimated Total</span>
            <strong>INR 0</strong>
          </div>
          <button className="gold-solid" disabled>Proceed to Checkout</button>
          <p>Free cancellation until 48 hours before check-in.</p>
        </aside>
      </section>
    </main>
  );
}

export function CheckoutModal({ room, query, onClose, onConfirm }) {
  const [form, setForm] = React.useState({
    checkInDate: query.checkInDate,
    checkOutDate: query.checkOutDate,
    rooms: query.numberOfRooms,
    guests: 2,
    specialRequests: ""
  });
  const [submitting, setSubmitting] = React.useState(false);

  const stayNights = nights({ checkInDate: form.checkInDate, checkOutDate: form.checkOutDate });
  const roomRate = Number(room?.estimatedAverageNightlyPrice || room?.basePrice || 0);
  const subtotal = roomRate * stayNights * Number(form.rooms || 1);
  const gst = subtotal * 0.18;
  const total = subtotal + gst;

  function update(event) {
    const { name, value } = event.target;
    setForm((current) => ({ ...current, [name]: value }));
  }

  async function submitPayment() {
    setSubmitting(true);
    try {
      await onConfirm({ ...form, finalCalculatedPrice: total });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="checkout-overlay">
      <div className="checkout-backdrop" onClick={onClose} />
      <section className="checkout-modal">
        <button className="modal-close" onClick={onClose}><X size={24} /></button>
        <div className="checkout-form">
          <p className="eyebrow">Reservation Details</p>
          <h2>Your Stay at <em>Roomly</em></h2>
          <div className="form-grid">
            <Field icon={CalendarDays} label="Check-in date">
              <input name="checkInDate" value={form.checkInDate} onChange={update} type="date" />
            </Field>
            <Field icon={CalendarDays} label="Check-out date">
              <input name="checkOutDate" value={form.checkOutDate} onChange={update} type="date" />
            </Field>
            <Field icon={BedDouble} label="Rooms">
              <input min="1" name="rooms" value={form.rooms} onChange={update} type="number" />
            </Field>
            <Field icon={Users} label="Guests">
              <input min="1" name="guests" value={form.guests} onChange={update} type="number" />
            </Field>
          </div>
          <label className="request-box">
            <span>Special Requests</span>
            <textarea name="specialRequests" value={form.specialRequests} onChange={update} placeholder="Dietary requirements, late check-in, or room scent preferences..." />
          </label>
          <p className="secure-line"><ShieldCheck size={15} /> Guaranteed secure reservation</p>
        </div>
        <aside className="price-panel">
          <h3>Reservation Summary</h3>
          <Line label={`${room?.type || "Selected room"} x ${stayNights} nights`} value={money(subtotal)} />
          <Line label="GST (18%)" value={money(gst)} muted />
          <div className="checkout-total">
            <span>Total Amount</span>
            <strong>{money(total)}</strong>
          </div>
          <button className="gold-solid" onClick={submitPayment} disabled={submitting}>
            {submitting ? "Opening Razorpay..." : "Pay with Razorpay"}
          </button>
          <p>By confirming, you agree to Roomly's editorial cancellation policy.</p>
        </aside>
      </section>
    </div>
  );
}

export function MyBookings({ bookings, navigate }) {
  const [activeTab, setActiveTab] = React.useState("Upcoming");
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  function bookingGroup(booking) {
    const status = String(booking.status || "").toUpperCase();
    if (status.includes("CANCEL")) return "Cancelled";

    const checkout = booking.checkoutDate ? new Date(booking.checkoutDate) : null;
    if (checkout && !Number.isNaN(checkout.getTime()) && checkout < today) return "Past";

    return "Upcoming";
  }

  const visibleBookings = bookings.filter((booking) => bookingGroup(booking) === activeTab);

  return (
    <main className="bookings-page">
      <header className="bookings-head">
        <h1>Your <em>Stays</em> with Roomly</h1>
        <p>A curated collection of past adventures and upcoming journeys.</p>
      </header>
      <div className="booking-tabs">
        {["Upcoming", "Past", "Cancelled"].map((tab) => (
          <button className={activeTab === tab ? "active" : ""} key={tab} onClick={() => setActiveTab(tab)} type="button">
            {tab}
          </button>
        ))}
      </div>
      <section className="booking-list">
        {visibleBookings.length ? (
          visibleBookings.map((booking) => <BookingCard booking={booking} key={booking.id} />)
        ) : (
          <div className="empty-bookings">
            <h3>No {activeTab.toLowerCase()} stays yet</h3>
            <p>{activeTab === "Upcoming" ? "Book a stay to see it here." : "Completed and cancelled reservations will be grouped here."}</p>
          </div>
        )}
      </section>
      <section className="escape-card">
        <BedDouble size={42} />
        <h3>Planning your next escape?</h3>
        <p>Discover the newest editorial stays curated for the season.</p>
        <button className="outline-button" onClick={() => navigate("hotels")}>Explore Destinations</button>
      </section>
    </main>
  );
}

export function StatusBadge({ status }) {
  const key = (status || "RESERVED").toUpperCase();
  const label = key === "GUESTS_ADDED" ? "Confirmed" : key.charAt(0) + key.slice(1).toLowerCase();
  return <span className={`status ${key.toLowerCase()}`}>{label}</span>;
}

function RoomSelectionCard({ room, featured, onSelect, disabled }) {
  return (
    <article className={featured ? "selection-card featured" : "selection-card"}>
      <img src={firstPhoto(room, img.roomA)} alt={room.type} />
      <div>
        <div className="selection-title">
          <div>
            {featured && <span className="exclusive">Most Exclusive</span>}
            <h3>{room.type}</h3>
          </div>
          <p>{money(room.estimatedAverageNightlyPrice || room.basePrice)} <span>per night</span></p>
        </div>
        <p className="room-desc">Elegant proportions, soft light, and the quiet confidence of a stay designed for arrival.</p>
        <div className="room-features">
          <span><UserRound size={15} /> {room.capacity || 2} Guests</span>
          <span><Wifi size={15} /> Free WiFi</span>
          {(room.amenities || []).slice(0, 2).map((amenity) => <span key={amenity}><Sparkles size={15} /> {amenity}</span>)}
        </div>
        <button className="forest-button" onClick={() => onSelect(room)} disabled={disabled}>
          {disabled ? "Guest Account Required" : "Select Room"}
        </button>
      </div>
    </article>
  );
}

function BookingCard({ booking }) {
  return (
    <article className="booking-card">
      <img src={booking.photo || img.heroLeft} alt={booking.hotelName} />
      <div>
        <div className="booking-top">
          <div>
            <h2>{booking.hotelName || "Roomly Stay"}</h2>
            <p><MapPin size={14} /> {booking.city}</p>
          </div>
          <StatusBadge status={booking.status} />
        </div>
        <div className="booking-meta">
          <div><span>Room Type</span><strong>{booking.roomType}</strong></div>
          <div><span>Date Range</span><strong>{booking.checkInDate} - {booking.checkoutDate}</strong></div>
        </div>
        <div className="booking-bottom">
          <span>Booking ID: #{booking.id}</span>
          <strong>{money(booking.finalCalculatedPrice)}</strong>
        </div>
      </div>
    </article>
  );
}

function Field({ icon: Icon, label, children }) {
  return (
    <label className="field">
      <span>{label}</span>
      <div>
        <Icon size={17} />
        {children}
      </div>
    </label>
  );
}

function Line({ label, value, muted }) {
  return (
    <div className={muted ? "price-line muted" : "price-line"}>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}
