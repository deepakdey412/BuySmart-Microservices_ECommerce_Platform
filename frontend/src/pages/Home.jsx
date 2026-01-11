import { Link } from 'react-router-dom'

const Home = () => {
  return (
    <div className="text-center py-16">
      <h1 className="text-5xl font-bold text-gray-800 mb-4">
        Welcome to E-Commerce Platform
      </h1>
      <p className="text-xl text-gray-600 mb-8">
        Discover amazing products at great prices
      </p>
      <Link
        to="/products"
        className="bg-blue-500 text-white px-8 py-3 rounded-lg text-lg hover:bg-blue-600"
      >
        Browse Products
      </Link>
    </div>
  )
}

export default Home