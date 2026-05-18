import React from "react";
import {
  BedDouble,
  CalendarDays,
  ChevronLeft,
  ChevronRight,
  ConciergeBell,
  Dumbbell,
  Heart,
  Hotel,
  MapPin,
  Search,
  ShieldCheck,
  Sparkles,
  Star,
  Utensils,
  Waves,
  Wifi
} from "lucide-react";
import { fallbackHotels, img } from "../data/roomlyContent";
import { firstPhoto, money } from "../lib/display";

export function Landing({ query, updateQuery, searchHotels, openHotel }) {
  return (
    <main>
      <section className="landing-hero">
        <div className="hero-copy">
          <p className="eyebrow">Curated stays</p>
          <h1>Find Your <em>Perfect</em> Stay</h1>
          <p>
            Experience the pinnacle of hospitality with live inventory, dynamic pricing, and a booking flow designed to feel like a private concierge.
          </p>
          <SearchBar query={query} updateQuery={updateQuery} onSubmit={searchHotels} />
          <div className="stats-row">
            <Stat value="2,400+" label="Hotels" />
            <Stat value="180+" label="Cities" />
            <Stat value="98%" label="Satisfaction" />
          </div>
        </div>
        <div className="hero-visual">
          <div className="building-mark">ROOMLY</div>
          <ImagePanel image={img.heroLeft} title="Kyoto" subtitle="Garden Suite" />
          <ImagePanel image={img.heroRight} title="Paris" subtitle="Grand Hotel" tall />
          <div className="glass-rating">4.9 <Star size={15} fill="currentColor" /></div>
        </div>
      </section>

      <section className="section editorial">
        <SectionHeader eyebrow="Discover" title={<>Featured <em>Collections</em></>} />
        <div className="featured-grid">
          {fallbackHotels.slice(0, 3).map((hotel) => (
            <HotelCard hotel={hotel} key={hotel.id} onOpen={openHotel} />
          ))}
        </div>
      </section>

      <HowItWorks />
      <RoomTypes />
      <Testimonials />
    </main>
  );
}

export function SearchResults({ hotels, page, query, updateQuery, searchHotels, applyRefineSearch, changePage, openHotel, loading }) {
  const currentPage = page?.number || 0;
  const totalPages = page?.totalPages || 1;
  const totalElements = page?.totalElements ?? hotels.length;
  const pages = Array.from({ length: totalPages }, (_, index) => index);
  const displayHotels = loading ? [] : hotels;

  if (loading) {
    return (
      <main className="search-page">
        <SearchBar compact query={query} updateQuery={updateQuery} onSubmit={searchHotels} />
        <div className="search-layout">
          <aside className="filters filters-skeleton" aria-hidden="true">
            <span className="skeleton-line title" />
            <span className="skeleton-line" />
            <span className="skeleton-line" />
            <span className="skeleton-line" />
            <div className="membership-skeleton skeleton-box" />
          </aside>
          <section className="results">
            <div className="results-head">
              <div>
                <span className="skeleton-line heading" />
                <span className="skeleton-line subheading" />
              </div>
              <span className="skeleton-button" />
            </div>
            <div className="results-grid">
              {Array.from({ length: 9 }).map((_, index) => <HotelCardSkeleton key={index} />)}
            </div>
          </section>
        </div>
      </main>
    );
  }

  return (
    <main className="search-page">
      <SearchBar compact query={query} updateQuery={updateQuery} onSubmit={searchHotels} />
      <div className="search-layout">
        <aside className="filters">
          <h3>Refine Search</h3>
          <FilterBlock label="Location">
            <select
              name="city"
              value={query.city}
              onChange={(event) => applyRefineSearch({ ...query, city: event.target.value })}
            >
              <option value="">All destinations</option>
              <option value="Mumbai">Mumbai</option>
              <option value="Goa">Goa</option>
              <option value="Jaipur">Jaipur</option>
              <option value="Delhi">Delhi</option>
            </select>
          </FilterBlock>
          <FilterBlock label="Price per night">
            <div className="range-copy"><span>INR 2,000</span><strong>INR 50,000</strong></div>
            <input type="range" min="2000" max="50000" defaultValue="50000" disabled />
          </FilterBlock>
          <FilterBlock label="Star Rating">
            {[5, 4].map((count) => (
              <label className="check-line" key={count}>
                <input type="checkbox" defaultChecked disabled />
                <span>{Array.from({ length: count }).map((_, i) => <Star key={i} size={13} fill="currentColor" />)}</span>
              </label>
            ))}
          </FilterBlock>
          <FilterBlock label="Amenities">
            {[
              [Wifi, "WiFi"],
              [Waves, "Pool"],
              [Utensils, "Breakfast"],
              [Dumbbell, "Gym"]
            ].map(([Icon, label]) => (
              <label className="check-line" key={label}>
                <input type="checkbox" disabled />
                <span><Icon size={15} /> {label}</span>
              </label>
            ))}
          </FilterBlock>
          <div className="membership-card">
            <img src={img.bath} alt="Luxury concierge desk" />
            <div>
              <span>Membership</span>
              <strong>Unlock exclusive member rates</strong>
            </div>
          </div>
        </aside>
        <section className="results">
          <div className="results-head">
            <div>
              <h1>Showing {totalElements || hotels.length || 0} Hotels <em>in {query.city || "all destinations"}</em></h1>
              <p>Page {currentPage + 1} of {totalPages}. Curated selections for the discerning traveler.</p>
            </div>
            <label className="sort-control">
              <span>Sort by:</span>
              <select defaultValue="Recommended">
                <option>Recommended</option>
                <option>Price: Low to High</option>
                <option>Price: High to Low</option>
                <option>Star Rating</option>
              </select>
            </label>
          </div>
          <div className="results-grid">
            {loading
              ? Array.from({ length: 6 }).map((_, index) => <HotelCardSkeleton key={index} />)
              : displayHotels.map((hotel) => <HotelCard hotel={hotel} key={hotel.id} onOpen={openHotel} />)}
          </div>
          {!loading && !displayHotels.length && (
            <div className="empty-results">
              <Hotel size={34} />
              <h2>No live stays returned</h2>
              <p>Try removing the city filter, checking the backend date range, or running the Neon smoke test.</p>
            </div>
          )}
          {!loading && totalPages >= 1 && (
            <div className="pagination">
              <button onClick={() => changePage(currentPage - 1)} disabled={currentPage === 0} type="button"><ChevronLeft size={18} /></button>
              {pages.map((pageNumber) => (
                <button className={pageNumber === currentPage ? "active" : ""} key={pageNumber} onClick={() => changePage(pageNumber)} type="button">
                  {pageNumber + 1}
                </button>
              ))}
              <button onClick={() => changePage(currentPage + 1)} disabled={currentPage >= totalPages - 1} type="button"><ChevronRight size={18} /></button>
            </div>
          )}
        </section>
      </div>
    </main>
  );
}

function HotelCardSkeleton() {
  return (
    <article className="hotel-card hotel-card-skeleton" aria-hidden="true">
      <div className="hotel-image skeleton-box" />
      <div className="hotel-body">
        <span className="skeleton-line short" />
        <span className="skeleton-line title" />
        <div className="pill-row">
          <span className="skeleton-pill" />
          <span className="skeleton-pill" />
          <span className="skeleton-pill" />
        </div>
        <div className="price-action">
          <span className="skeleton-line price" />
          <span className="skeleton-button" />
        </div>
      </div>
    </article>
  );
}

function SearchBar({ query, updateQuery, onSubmit, compact = false }) {
  return (
    <form className={compact ? "searchbar compact" : "searchbar"} onSubmit={onSubmit}>
      <Field icon={MapPin} label="Destination">
        <input name="city" value={query.city} onChange={updateQuery} placeholder="Where to next?" />
      </Field>
      <Field icon={CalendarDays} label="Check-in">
        <input name="checkInDate" value={query.checkInDate} onChange={updateQuery} type="date" />
      </Field>
      <Field icon={CalendarDays} label="Check-out">
        <input name="checkOutDate" value={query.checkOutDate} onChange={updateQuery} type="date" />
      </Field>
      <Field icon={BedDouble} label="Rooms">
        <input min="1" name="numberOfRooms" value={query.numberOfRooms} onChange={updateQuery} type="number" />
      </Field>
      <button className="gold-button" type="submit">
        <Search size={18} />
      </button>
    </form>
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

function Stat({ value, label }) {
  return (
    <div className="stat">
      <strong>{value}</strong>
      <span>{label}</span>
    </div>
  );
}

function ImagePanel({ image, title, subtitle, tall }) {
  return (
    <article className={tall ? "image-panel tall" : "image-panel"}>
      <img src={image} alt={`${title} ${subtitle}`} />
      <div>
        <strong>{title}</strong>
        <span>{subtitle}</span>
      </div>
    </article>
  );
}

function SectionHeader({ eyebrow, title, inverse = false }) {
  return (
    <div className={inverse ? "section-header inverse" : "section-header"}>
      <p className="eyebrow">{eyebrow}</p>
      <h2>{title}</h2>
    </div>
  );
}

function HotelCard({ hotel, onOpen }) {
  const hotelName = hotel.hotelName || hotel.name || `Roomly ${hotel.city}`;
  const hotelAddress = hotel.address || hotel.contactInfo?.completeAddress || hotel.contactInfo?.location || hotel.city;
  return (
    <article className="hotel-card">
      <div className="hotel-image">
        <img src={firstPhoto(hotel, img.searchA)} alt={`${hotelName} hotel`} />
        <span className="availability">Available Stay</span>
        <button className="wish" type="button" aria-label="Wishlist"><Heart size={18} /></button>
      </div>
      <div className="hotel-body">
        <p className="pin"><MapPin size={14} /> {hotelAddress}</p>
        <div className="hotel-title-line">
          <h3>{hotelName}</h3>
          <span className="rating"><Star size={14} fill="currentColor" /> {hotel.rating || 4.9}</span>
        </div>
        <div className="pill-row">
          {(hotel.amenities || []).slice(0, 3).map((amenity) => <span key={amenity}>{amenity}</span>)}
        </div>
        <div className="price-action">
          <p>{money(hotel.estimatedStartingPrice)} <span>/night</span></p>
          <button className="outline-button" onClick={() => onOpen(hotel)} type="button">Book Now</button>
        </div>
      </div>
    </article>
  );
}

function HowItWorks() {
  const steps = [
    ["01", Search, "Curated Selection", "Browse live availability from hotels configured in your backend."],
    ["02", CalendarDays, "Instant Booking", "Choose dates, room count, and rooms with availability checks."],
    ["03", ConciergeBell, "Dynamic Pricing", "Urgency, occupancy, surge, and holidays shape the quote."],
    ["04", ShieldCheck, "Inventory Lock", "Pessimistic locking protects reservations from double booking."]
  ];

  return (
    <section className="how">
      <div className="watermark">ROOMLY</div>
      <SectionHeader eyebrow="The experience" title={<>How Roomly <em>Works</em></>} inverse />
      <div className="steps-grid">
        {steps.map(([number, Icon, title, text]) => (
          <article key={number}>
            <span>{number}</span>
            <div><Icon size={26} /></div>
            <h3>{title}</h3>
            <p>{text}</p>
          </article>
        ))}
      </div>
    </section>
  );
}

function RoomTypes() {
  const cards = [
    ["The Presidential Suite", "Unparalleled Luxury at New Heights", "INR 18,400 / night", img.roomB, "large"],
    ["The Heritage Room", "Classic Elegance", "INR 8,200 / night", img.roomA, ""],
    ["The Garden Terrace", "Nature Infused", "INR 12,800 / night", img.roomC, ""]
  ];

  return (
    <section className="room-types">
      <div className="room-types-head">
        <SectionHeader eyebrow="Refined spaces" title={<>Signature <em>Living</em></>} />
        <div className="tabs">
          {["All", "Standard", "Deluxe", "Suite"].map((tab, index) => <button className={index === 0 ? "active" : ""} key={tab}>{tab}</button>)}
        </div>
      </div>
      <div className="asymmetric-grid">
        {cards.map(([label, title, price, image, size]) => (
          <article className={size === "large" ? "room-type-card large" : "room-type-card"} key={label}>
            <img src={image} alt={label} />
            <div>
              <span>{label}</span>
              <h3>{title}</h3>
              <p>{price}</p>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

function Testimonials() {
  const items = [
    ["Eliza Montgomery", "Mumbai", img.avatarA, "Roomly transformed our anniversary trip into a cinematic experience. Every detail felt intentional."],
    ["Julian Vance", "Delhi", img.avatarB, "The booking flow and curated room choices feel calmer and richer than any standard booking app."],
    ["Sara David", "Goa", img.avatarC, "From the first search to the final quote, the whole stay felt thoughtfully composed."]
  ];

  return (
    <section className="testimonials">
      <SectionHeader eyebrow="Guest stories" title={<>Voices of <em>Roomly</em></>} />
      <div className="testimonial-grid">
        {items.map(([name, city, avatar, quote]) => (
          <article key={name}>
            <span className="quote-mark">"</span>
            <div className="stars">{Array.from({ length: 5 }).map((_, i) => <Star key={i} size={14} fill="currentColor" />)}</div>
            <p>{quote}</p>
            <div className="author">
              <img src={avatar} alt={name} />
              <div>
                <strong>{name}</strong>
                <span>{city}</span>
              </div>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

function FilterBlock({ label, children }) {
  return (
    <div className="filter-block">
      <label>{label}</label>
      {children}
    </div>
  );
}
