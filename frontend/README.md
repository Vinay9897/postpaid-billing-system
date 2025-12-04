# Postpaid Billing System - Frontend

React-based frontend for the ABC Telecom Postpaid Billing System.

## Prerequisites

- **Node.js 18+** and **npm** or **yarn**
- **Backend running** at `http://localhost:8080`

## Quick Start

### 1. Install Dependencies

```powershell
cd frontend
npm install
```

### 2. Start Development Server

```powershell
npm run dev
```

The frontend will be available at `http://localhost:3000`

### 3. Build for Production

```powershell
npm run build
```

Output will be in the `dist/` folder.

### 4. Preview Production Build

```powershell
npm run preview
```

## Project Structure

```
frontend/
├── package.json
├── vite.config.js
├── index.html
├── src/
│   ├── main.jsx              # Entry point
│   ├── App.jsx               # Main App component
│   ├── App.css               # App styles
│   ├── index.css             # Global styles
│   ├── components/           # Reusable components (to be added)
│   │   ├── Login/
│   │   ├── Register/
│   │   ├── Dashboard/
│   │   └── ...
│   ├── pages/                # Page components (to be added)
│   │   ├── LoginPage.jsx
│   │   ├── DashboardPage.jsx
│   │   └── ...
│   ├── api/                  # API client configuration (to be added)
│   │   └── client.js
│   ├── services/             # Business logic (to be added)
│   │   ├── authService.js
│   │   ├── customerService.js
│   │   └── ...
│   ├── store/                # Zustand state management (to be added)
│   │   └── authStore.js
│   └── utils/                # Utility functions (to be added)
│       └── validators.js
└── dist/                      # Build output
```

## Features (by Step)

### Step 0: Project Setup (Current)
- ✅ React 18 with Vite
- ✅ React Router for navigation
- ✅ Axios for API calls
- ✅ Zustand for state management
- ✅ Vite proxy to backend

### Step 1: Authentication
- Login form
- Registration form
- JWT token storage
- Protected routes

### Step 2-7: Feature Pages
- User management (admin)
- Customer profile
- Service/subscription management
- Usage tracking
- Invoice management
- Payment processing

## Available Scripts

```powershell
# Development server (hot reload)
npm run dev

# Build for production
npm run build

# Preview production build locally
npm run preview

# Run linter
npm run lint

# Run tests
npm run test
```

## API Integration

The development server proxies `/api` requests to `http://localhost:8080`:

```javascript
// requests to /api/login are sent to http://localhost:8080/api/login
axios.post('/api/login', { username, password })
```

## Authentication Flow

1. User registers or logs in
2. Backend returns JWT token
3. Token stored in Zustand store (and localStorage)
4. Token included in Authorization header for protected requests
5. Token verified by backend on each request

## State Management (Zustand)

```javascript
// Example usage (to be implemented in Step 1)
import { useAuthStore } from './store/authStore'

function MyComponent() {
  const { user, login, logout } = useAuthStore()
  // ...
}
```

## Styling

- Global styles in `src/index.css`
- Component-specific styles in respective `.css` files
- Mobile-responsive design (breakpoint at 768px)

## Environment Variables

Create `.env.local` for environment-specific settings:

```
VITE_API_BASE_URL=http://localhost:8080/api
```

Then use in code:

```javascript
const apiBaseUrl = import.meta.env.VITE_API_BASE_URL
```

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Testing

Tests use **Vitest** and **React Testing Library**:

```powershell
# Run tests
npm run test

# Run tests in watch mode
npm run test -- --watch

# Run tests with coverage
npm run test -- --coverage
```

## Development Guidelines

### Component Structure
```jsx
// src/components/MyComponent/MyComponent.jsx
import './MyComponent.css'

export default function MyComponent() {
  return <div className="my-component">{/* ... */}</div>
}
```

### API Calls (Step 1+)
```javascript
// src/services/authService.js
import axios from 'axios'

export const login = async (username, password) => {
  const response = await axios.post('/api/login', { username, password })
  return response.data
}
```

### Zustand Store (Step 1+)
```javascript
// src/store/authStore.js
import { create } from 'zustand'

export const useAuthStore = create((set) => ({
  token: null,
  setToken: (token) => set({ token }),
}))
```

## Troubleshooting

### Port 3000 Already in Use
Edit `vite.config.js` and change `server.port` to another port.

### Backend Proxy Not Working
- Ensure backend is running on `http://localhost:8080`
- Check `vite.config.js` proxy configuration
- Check browser Network tab for CORS issues

### Node Modules Issues
```powershell
rm -r node_modules package-lock.json
npm install
```

## Next Steps

1. **Step 1**: Build login and registration pages with JWT handling
2. **Step 2**: Build admin user management pages
3. **Step 3**: Build customer profile pages
4. Continue through Steps 4-8

## References

- [React 18 Documentation](https://react.dev)
- [Vite Documentation](https://vitejs.dev)
- [React Router Documentation](https://reactrouter.com)
- [Axios Documentation](https://axios-http.com)
- [Zustand Documentation](https://github.com/pmndrs/zustand)
