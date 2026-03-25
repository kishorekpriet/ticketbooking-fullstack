import { useState, useEffect } from 'react';
import './App.css';

function App() {
  const [showBookingPage, setShowBookingPage] = useState(false);
  const [selectedSeats, setSelectedSeats] = useState([]);
  
  // NEW: State to remember which seats the database says are taken
  const [bookedSeats, setBookedSeats] = useState([]); 
  
  const totalSeats = Array.from({ length: 20 }, (_, i) => i + 1);

  // NEW: This runs automatically when the booking page opens
  useEffect(() => {
    if (showBookingPage) {
      fetchSeats();
    }
  }, [showBookingPage]);

  const fetchSeats = async () => {
    try {
      const response = await fetch('http://localhost:8081/api/seats');
      const data = await response.json();
      
      console.log("🔥 RAW DATA FROM SPRING BOOT:", data);
      
      const takenSeatIds = [];
      
      // Loop through whatever Spring Boot sent us
      data.forEach(seat => {
        // Checking every possible way Java might spell "booked"
        if (seat.booked === true || seat.isBooked === true) {
          takenSeatIds.push(seat.id);
        }
      });
        
      console.log("🔒 THESE SEAT IDs SHOULD BE GRAY:", takenSeatIds);
      setBookedSeats(takenSeatIds);
      
    } catch (error) {
      console.error("❌ Error fetching seats:", error);
    }
  };

  const toggleSeat = (seatId) => {
    // SECURITY: If the seat is already booked, do absolutely nothing when clicked
    if (bookedSeats.includes(seatId)) return;

    if (selectedSeats.includes(seatId)) {
      setSelectedSeats(selectedSeats.filter(id => id !== seatId));
    } else {
      setSelectedSeats([...selectedSeats, seatId]);
    }
  };

  const calculateTotal = () => {
    let total = 0;
    selectedSeats.forEach(seat => {
      if (seat <= 5) total += 250; 
      else total += 150; 
    });
    return total;
  };

  const handlePayment = async () => {
    for (const seatId of selectedSeats) {
      try {
        const response = await fetch(`http://localhost:8081/api/tickets/book?movieId=1&seatId=${seatId}&userEmail=guest@cinema.com`, {
          method: 'POST'
        });

        if (response.ok) {
          alert(`✅ Success! Seat ${seatId} has been booked.`);
        } else {
          const errorMessage = await response.text();
          alert(`❌ Failed to book Seat ${seatId}: ${errorMessage}`);
        }
      } catch (error) {
        alert("Error connecting to the server. Is Spring Boot running?");
      }
    }
    
    // Clear the cart and refresh the seat map to instantly turn them gray!
    setSelectedSeats([]);
    fetchSeats(); 
  };

  // --- RENDERING THE ENTRY PAGE ---
  if (!showBookingPage) {
    return (
      <div className="entry-page">
        <div className="header">
          <h2>BookYourTicket Kishore</h2>
        </div>
        <div className="hero-banner">
          <div className="hero-content">
            <h1 className="movie-title">Inception (U/A)</h1>
            <div className="movie-tags">
              <span className="tag">Action</span>
              <span className="tag">Sci-Fi</span>
              <span className="tag">Thriller</span>
              <span className="tag">2h 28m</span>
            </div>
            <p className="movie-description">
              A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster.
            </p>
            <button className="book-now-btn" onClick={() => setShowBookingPage(true)}>
              Book Tickets
            </button>
          </div>
        </div>
      </div>
    );
  }

  // --- RENDERING THE SEAT MATRIX PAGE ---
  return (
    <div>
      <div className="header">
        <h2>Inception (U/A)</h2>
        <p>PVR Cinemas: Brookefields Mall, Coimbatore | Today, 24 Mar, 07:00 PM</p>
      </div>

      <div className="seat-map-container">
        <div className="category-title">Premium - Rs. 250.00</div>
        <div className="seat-row">
          <div className="row-label">A</div>
          {totalSeats.slice(0, 5).map(seatId => (
            <div 
              key={seatId}
              // NEW: We add the 'booked' CSS class if the seat is in our bookedSeats array!
              className={`seat ${selectedSeats.includes(seatId) ? 'selected' : ''} ${bookedSeats.includes(seatId) ? 'booked' : ''}`}
              onClick={() => toggleSeat(seatId)}
            >{seatId}</div>
          ))}
        </div>

        <div className="category-title" style={{ marginTop: '30px' }}>Standard - Rs. 150.00</div>
        {['B', 'C', 'D'].map((rowLetter, rowIndex) => (
          <div className="seat-row" key={rowLetter}>
            <div className="row-label">{rowLetter}</div>
            {totalSeats.slice(5 + (rowIndex * 5), 10 + (rowIndex * 5)).map(seatId => (
              <div 
                key={seatId}
                // NEW: Adding the 'booked' class here too
                className={`seat ${selectedSeats.includes(seatId) ? 'selected' : ''} ${bookedSeats.includes(seatId) ? 'booked' : ''}`}
                onClick={() => toggleSeat(seatId)}
              >{seatId}</div>
            ))}
          </div>
        ))}

        <div className="screen-container">
          <div className="screen"></div>
          <div className="screen-text">All eyes this way please!</div>
        </div>
      </div>

      {selectedSeats.length > 0 && (
        <div className="footer">
          <button className="pay-btn" onClick={handlePayment}>
            Pay Rs. {calculateTotal()}.00
          </button>
        </div>
      )}
    </div>
  );
}

export default App;