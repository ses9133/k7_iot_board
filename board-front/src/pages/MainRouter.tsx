import React from 'react'
import { Route, Routes } from 'react-router-dom'
import BoardListPage from './board/BoardListPage'

function MainRouter() {
  return (
    <Routes>
      {/* 로그인 이후 게시판 라우터 */}
      <Route path="/board" element={<BoardListPage />} />

      
      <Route path="*" element={<BoardListPage />} />
    </Routes>
  )
}

export default MainRouter 