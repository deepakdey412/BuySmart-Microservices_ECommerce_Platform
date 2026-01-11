const AdminDashboard = () => {
  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Admin Dashboard</h1>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-xl font-semibold mb-4">Product Management</h2>
          <p className="text-gray-600">Manage products and categories</p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-xl font-semibold mb-4">Inventory Management</h2>
          <p className="text-gray-600">Manage inventory and stock</p>
        </div>
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-xl font-semibold mb-4">Order Analytics</h2>
          <p className="text-gray-600">View order statistics and analytics</p>
        </div>
      </div>
    </div>
  )
}

export default AdminDashboard