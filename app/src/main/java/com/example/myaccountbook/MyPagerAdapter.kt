package com.example.myaccountbook

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

// viewPager2 의 어댑터를 생성해서 viewPager2 사용
// FragmentActivity 를 인자로 받음
class MyPagerAdapter(fa:FragmentActivity) : FragmentStateAdapter(fa) {
    // 원하는 페이지 수 설정
    private val NUM_PAGES = 3
    override fun getItemCount(): Int = NUM_PAGES

    // 각 페이지가 어떤 fragment 로 만들어질지 내용 전달
    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> BudgetFragment()
            1 -> RecordFragment_bc()
            else -> ChallengeFragment()
        }
    }

}