import { useState, useEffect } from 'react'
import axios from '../api/api'
import { useAuth } from '../context/AuthContext'

const Orders = () => {
  const { user } = useAuth()
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (user) {
      fetchOrders()
    }
  }, [user])

  const fetchOrders = async () => {
    try {
      const response = await axios.get('/api/orders/user', {
        headers: {
          'X-User-Id': user.userId || user.id
        }
      })
      setOrders(response.data)
    } catch (error) {
      console.error('Failed to fetch orders:', error)
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return <div className="text-center py-8">Loading orders...</div>
  }

  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">My Orders</h1>
      {orders.length === 0 ? (
        <div className="text-center py-8">
          <p>You have no orders yet.</p>
        </div>
      ) : (
        <div className="space-y-4">
          {orders.map((order) => (
            <div key={order.id} className="bg-white rounded-lg shadow-md p-6">
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h3 className="text-xl font-semibold">Order #{order.id}</h3>
                  <p className="text-gray-600">
                    Status: <span className="font-semibold">{order.status}</span>
                  </p>
                  <p className="text-gray-600">
                    Date: {new Date(order.createdAt).toLocaleDateString()}
                  </p>
                </div>
                <p className="text-2xl font-bold text-blue-600">
                  ${order.totalAmount}
                </p>
              </div>
              <div>
                <h4 className="font-semibold mb-2">Items:</h4>
                <ul className="list-disc list-inside">
                  {order.items.map((item) => (
                    <li key={item.id}>
                      Product ID: {item.productId} - Quantity: {item.quantity} - ${item.subtotal}
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Orders