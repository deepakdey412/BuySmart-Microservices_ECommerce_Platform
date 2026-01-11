import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from '../api/api'
import { useAuth } from '../context/AuthContext'

const Cart = () => {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [cart, setCart] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (user) {
      fetchCart()
    }
  }, [user])

  const fetchCart = async () => {
    try {
      const response = await axios.get('/api/cart', {
        headers: {
          'X-User-Id': user.userId || user.id
        }
      })
      setCart(response.data)
    } catch (error) {
      console.error('Failed to fetch cart:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleRemoveItem = async (productId) => {
    try {
      await axios.delete(`/api/cart/items/${productId}`, {
        headers: {
          'X-User-Id': user.userId || user.id
        }
      })
      fetchCart()
    } catch (error) {
      console.error('Failed to remove item:', error)
    }
  }

  const handleCheckout = () => {
    // Navigate to checkout/order creation
    navigate('/orders')
  }

  if (loading) {
    return <div className="text-center py-8">Loading cart...</div>
  }

  if (!cart || !cart.items || cart.items.length === 0) {
    return (
      <div className="text-center py-8">
        <h1 className="text-2xl font-bold mb-4">Your Cart is Empty</h1>
        <button
          onClick={() => navigate('/products')}
          className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600"
        >
          Browse Products
        </button>
      </div>
    )
  }

  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Shopping Cart</h1>
      <div className="bg-white rounded-lg shadow-md p-6">
        {cart.items.map((item) => (
          <div
            key={item.id}
            className="flex justify-between items-center py-4 border-b"
          >
            <div>
              <h3 className="font-semibold">Product ID: {item.productId}</h3>
              <p>Quantity: {item.quantity}</p>
              <p>Price: ${item.price}</p>
              <p>Subtotal: ${item.subtotal}</p>
            </div>
            <button
              onClick={() => handleRemoveItem(item.productId)}
              className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
            >
              Remove
            </button>
          </div>
        ))}
        <div className="mt-6 flex justify-between items-center">
          <h2 className="text-2xl font-bold">
            Total: ${cart.totalAmount}
          </h2>
          <button
            onClick={handleCheckout}
            className="bg-green-500 text-white px-8 py-3 rounded-lg hover:bg-green-600"
          >
            Checkout
          </button>
        </div>
      </div>
    </div>
  )
}

export default Cart